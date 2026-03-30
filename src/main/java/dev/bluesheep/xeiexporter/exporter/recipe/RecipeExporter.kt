package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.recipe.IRecipeExporter
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.result.ItemRecipeResult
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlJei
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlVanilla
import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.RecipeTypeTable
import dev.bluesheep.xeiexporter.sql.RecipesTable
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.jvm.optionals.getOrElse

class RecipeExporter {
    private val blackList = listOf(
        rlJei("debug"),
        rlJei("debug_focus"),
        rlJei("obnoxiously_large_recipe"),
        rlVanilla("tag_recipes/block"),
        rlVanilla("tag_recipes/fluid"),
        rlVanilla("tag_recipes/item")
    )

    fun exportRecipes() {
        val runtime = JEIExporterPlugin.runtime ?: return

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

                val recipeId = category.getRegistryName(recipe)
                    ?: ExportUtil.rl("${recipeTypeId.toString().replace(':', '_')}/")
                return@map RecipeData(
                    recipeId,
                    recipeTypeId,
                    slotsView.getSlotViews(RecipeIngredientRole.INPUT).map {
                        ItemRecipeIngredient(it.itemStacks.toList())
                    },
                    slotsView.getSlotViews(RecipeIngredientRole.OUTPUT).map {
                        ItemRecipeResult(if (it.itemStacks.findAny().isPresent) it.itemStacks.toList().first() else ItemStack.EMPTY)
                    }
                )
            }
        }.toList()

        // レシピタイプの出力
        val recipeTypes = mutableListOf<RecipeTypeData>()
        runtime.jeiHelpers.allRecipeTypes.forEach { recipeType ->
            if (blackList.contains(recipeType.uid)) return@forEach
            val catalyst = runtime.recipeManager.createRecipeCatalystLookup(recipeType).get()
                .map { it.itemStack.getOrElse { ItemStack.EMPTY } }.toList()

            val slot = recipeTypeSlots[recipeType.uid]!!
            recipeTypes.add(RecipeTypeData(
                recipeType.uid,
                ItemRecipeIngredient(catalyst),
                slot.first,
                slot.second
            ))
        }

        // DBに書き込み
        transaction {
            DatabaseUtil.reset(RecipesTable)

            RecipesTable.batchInsert(recipes) {
                this[RecipesTable.namespace] = it.id.namespace
                this[RecipesTable.path] = it.id.path
                this[RecipesTable.type] = it.type.toString()
                this[RecipesTable.input] = it.input.map { input -> input.export().joinToString(",") }
                this[RecipesTable.output] = it.output.map { output -> output.export() }
            }

            DatabaseUtil.reset(RecipeTypeTable)

            RecipeTypeTable.batchInsert(recipeTypes) {
                this[RecipeTypeTable.id] = it.id.toString()
                this[RecipeTypeTable.catalyst] = it.catalyst.export()
                this[RecipeTypeTable.inputSize] = it.inputSize
                this[RecipeTypeTable.outputSize] = it.outputSize
            }
        }
    }

    private data class RecipeTypeData(
        val id: ResourceLocation,
        val catalyst: ItemRecipeIngredient,
        val inputSize: Int,
        val outputSize: Int
    ) {
        companion object {
            val EMPTY = RecipeTypeData(rlVanilla("empty"), ItemRecipeIngredient(ItemStack.EMPTY), 0, 0)
        }
    }
}
