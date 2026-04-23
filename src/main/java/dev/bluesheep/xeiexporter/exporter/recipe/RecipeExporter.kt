package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.RecipeStackData
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlJei
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlVanilla
import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.RecipeTypeTable
import dev.bluesheep.xeiexporter.sql.RecipesTable
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceLocation
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class RecipeExporter {
    private val blackList = listOf(
        rlJei("debug"),
        rlJei("debug_focus"),
        rlJei("obnoxiously_large_recipe"),
        rlVanilla("tag_recipes/block"),
        rlVanilla("tag_recipes/fluid"),
        rlVanilla("tag_recipes/item")
    )

    fun exportRecipes(): Int {
        val runtime = JEIExporterPlugin.runtime ?: return 0

        val recipeTypeSlots = mutableMapOf<ResourceLocation, Pair<Int, Int>>()
        // 登録されているレシピをRecipeDataとして出力
        val recipes = runtime.jeiHelpers.allRecipeTypes.flatMap { recipeType ->
            if (blackList.contains(recipeType.uid)) return@flatMap null
            // 再度RecipeTypeを取得することで型を適切なものにする
            val castRecipeType: RecipeType<Any> = runtime.recipeManager.getRecipeType(recipeType.uid, recipeType.recipeClass).get()
            val recipeTypeId = castRecipeType.uid

            val category = runtime.recipeManager.getRecipeCategory(castRecipeType)
            val recipeLookup = runtime.recipeManager.createRecipeLookup(castRecipeType).get()

            recipeTypeSlots.put(recipeTypeId, Pair(0, 0))

            return@flatMap recipeLookup.map { recipe ->
                val layout = runtime.recipeManager.createRecipeLayoutDrawable(
                    category,
                    recipe,
                    runtime.jeiHelpers.focusFactory.emptyFocusGroup
                )

                val slotsView = layout.get().recipeSlotsView
                // スロットの数を保持しておく
                val slotsCount = recipeTypeSlots[recipeTypeId]!!
                val inputSize = slotsView.getSlotViews(RecipeIngredientRole.INPUT).count()
                val outputSize = slotsView.getSlotViews(RecipeIngredientRole.OUTPUT).count()
                if (slotsCount.first < inputSize) {
                    recipeTypeSlots.put(recipeTypeId, slotsCount.copy(first = inputSize))
                }
                if (slotsCount.second < outputSize) {
                    recipeTypeSlots.put(recipeTypeId, slotsCount.copy(second = outputSize))
                }

                val input = slotsView.getSlotViews(RecipeIngredientRole.INPUT).map { slot ->
                    RecipeSlot.createFrom(slot.allIngredients.toList())
                }
                val output = slotsView.getSlotViews(RecipeIngredientRole.OUTPUT).map { slot ->
                    RecipeSlot.fromTypedIngredients(slot.allIngredients.toList())
                }

                val recipeId = category.getRegistryName(recipe)
                    ?: ExportUtil.rl("${recipeTypeId.toString().replace(':', '_')}/")
                return@map RecipeData(
                    recipeId,
                    recipeTypeId,
                    input,
                    output
                )
            }
        }.toList()

        // レシピタイプの出力
        val recipeTypes = mutableListOf<RecipeTypeData>()
        runtime.jeiHelpers.allRecipeTypes.forEach { recipeType ->
            if (blackList.contains(recipeType.uid)) return@forEach
            val catalyst = RecipeSlot.fromTypedIngredients(
                runtime.recipeManager.createRecipeCatalystLookup(recipeType).get().toList()
            )
            val title = runtime.recipeManager.getRecipeCategory(recipeType).title
            val titleComponent = title.contents
            val titleKey = if (titleComponent is TranslatableContents) {
                titleComponent.key
            } else ""
            val titleFallback = if (titleComponent is TranslatableContents) {
                titleComponent.fallback ?: ""
            } else title.string

            val slot = recipeTypeSlots[recipeType.uid]!!
            recipeTypes.add(RecipeTypeData(
                recipeType.uid,
                catalyst,
                slot.first,
                slot.second,
                titleKey,
                titleFallback
            ))
        }

        // DBに書き込み
        transaction {
            DatabaseUtil.reset(RecipesTable)

            RecipesTable.batchInsert(recipes) {
                this[RecipesTable.namespace] = it.id.namespace
                this[RecipesTable.path] = it.id.path
                this[RecipesTable.type] = it.type.toString()
                this[RecipesTable.input] = it.input
                this[RecipesTable.output] = it.output
            }

            DatabaseUtil.reset(RecipeTypeTable)

            RecipeTypeTable.batchInsert(recipeTypes) {
                this[RecipeTypeTable.id] = it.id.toString()
                this[RecipeTypeTable.catalyst] = it.catalyst
                this[RecipeTypeTable.inputSize] = it.inputSize
                this[RecipeTypeTable.outputSize] = it.outputSize
                this[RecipeTypeTable.titleKey] = it.titleKey
                this[RecipeTypeTable.titleFallback] = it.titleFallback
            }
        }

        return recipes.size
    }

    private data class RecipeTypeData(
        val id: ResourceLocation,
        val catalyst: List<RecipeStackData>,
        val inputSize: Int,
        val outputSize: Int,
        val titleKey: String,
        val titleFallback: String
    )
}
