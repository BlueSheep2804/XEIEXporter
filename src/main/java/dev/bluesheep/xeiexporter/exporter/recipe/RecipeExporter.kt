package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.ExporterJeiPlugin
import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.api.recipe.IRecipeExporter
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import dev.bluesheep.xeiexporter.exporter.ExportUtil.mkdir
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlVanilla
import dev.bluesheep.xeiexporter.exporter.ExportUtil.saveExportFile
import dev.bluesheep.xeiexporter.sql.RecipeTypeTable
import dev.bluesheep.xeiexporter.sql.RecipesTable
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraftforge.registries.ForgeRegistries
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.exists
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.nio.file.Path

class RecipeExporter {
    companion object {
        private val EXPORT_RECIPES_DIR: Path = XEIExporter.EXPORT_DIR.resolve("recipes")
        private val EXPORT_RECIPE_TYPE_FILE: Path = EXPORT_RECIPES_DIR.resolve("recipe_types.json")
    }

    private val recipeExporterRegistry = mutableMapOf<Class<out Recipe<*>>, IRecipeExporter<*>>()

    init {
        recipeExporterRegistry.put(SmeltingRecipe::class.java, CookingRecipeExporter<SmeltingRecipe>(rlVanilla("smelting")))
        recipeExporterRegistry.put(BlastingRecipe::class.java, CookingRecipeExporter<BlastingRecipe>(rlVanilla("blasting")))
        recipeExporterRegistry.put(SmokingRecipe::class.java, CookingRecipeExporter<SmokingRecipe>(rlVanilla("smoking")))
        recipeExporterRegistry.put(CraftingRecipe::class.java, CraftingRecipeExporter())
    }

    fun exportRecipes() {
        val level = Minecraft.getInstance().level ?: return

        mkdir(EXPORT_RECIPES_DIR)

        val recipeTypes = ForgeRegistries.RECIPE_TYPES.entries.map { (key, _) -> key.location().toString() }
        saveExportFile(recipeTypes, EXPORT_RECIPE_TYPE_FILE)

        // Exporterが登録されているレシピをRecipeDataとして出力
        val recipes = mutableListOf<RecipeData>()
        level.recipeManager.recipes.forEach { recipe ->
            val exporter = recipeExporterRegistry.filter { (clazz, _) -> clazz.isAssignableFrom(recipe.javaClass) }.values.firstOrNull() ?: return@forEach
            val recipeData = (exporter as? IRecipeExporter<Recipe<*>>)?.export(recipe, level) ?: return@forEach
            recipes.add(recipeData)
        }

        // DBに書き込み
        transaction {
            if (RecipesTable.exists()) {
                SchemaUtils.drop(RecipesTable)
            }
            SchemaUtils.create(RecipesTable)
            RecipesTable.batchInsert(recipes) {
                this[RecipesTable.namespace] = it.id.namespace
                this[RecipesTable.path] = it.id.path
                this[RecipesTable.type] = it.type.toString()
                this[RecipesTable.input] = it.input.map { input -> input.export().joinToString() }
                this[RecipesTable.output] = it.output.map { output -> output.export() }
            }

            if (RecipeTypeTable.exists()) {
                SchemaUtils.drop(RecipeTypeTable)
            }
            SchemaUtils.create(RecipeTypeTable)

            val runtime = ExporterJeiPlugin.runtime ?: return@transaction
            val recipeTypes = runtime.jeiHelpers.allRecipeTypes.toList().map { recipeType ->
                val catalyst = runtime.recipeManager.createRecipeCatalystLookup(recipeType).get().map { it.itemStack.get() }.toList()
                val exporter = recipeExporterRegistry[recipeType.recipeClass] ?: return@map RecipeTypeData.EMPTY
                return@map RecipeTypeData(
                    exporter.recipeTypeId,
                    ItemRecipeIngredient(catalyst),
                    exporter.inputSize,
                    exporter.outputSize
                )
            }
            RecipeTypeTable.batchInsert(recipeTypes.filterNot { it == RecipeTypeData.EMPTY }) {
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
