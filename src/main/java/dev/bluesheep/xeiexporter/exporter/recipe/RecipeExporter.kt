package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.api.recipe.IRecipeExporter
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.exporter.ExportUtil.mkdir
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlVanilla
import dev.bluesheep.xeiexporter.exporter.ExportUtil.saveExportFile
import dev.bluesheep.xeiexporter.sql.RecipesTable
import net.minecraft.client.Minecraft
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
        recipeExporterRegistry.put(ShapelessRecipe::class.java, ShapelessRecipeExporter())
    }

    fun exportRecipes() {
        val level = Minecraft.getInstance().level ?: return

        mkdir(EXPORT_RECIPES_DIR)

        val recipeTypes = ForgeRegistries.RECIPE_TYPES.entries.map { (key, _) -> key.location().toString() }
        saveExportFile(recipeTypes, EXPORT_RECIPE_TYPE_FILE)

        // Exporterが登録されているレシピをRecipeDataとして出力
        val recipes = mutableListOf<Pair<Class<out Recipe<*>>, RecipeData>>()
        level.recipeManager.recipes.forEach { recipe ->
            val exporter = recipeExporterRegistry[recipe.javaClass]
            val recipeData = (exporter as? IRecipeExporter<Recipe<*>>)?.export(recipe, level) ?: return@forEach
            recipes.add(recipe.javaClass to recipeData)
        }

        // DBに書き込み
        transaction {
            if (RecipesTable.exists()) {
                SchemaUtils.drop(RecipesTable)
            }
            SchemaUtils.create(RecipesTable)
            recipeExporterRegistry.forEach { (recipeClass, _) ->
                val filteredRecipe = recipes.filter { it.first == recipeClass }.map { it.second }
                RecipesTable.batchInsert(filteredRecipe) {
                    this[RecipesTable.namespace] = it.id.namespace
                    this[RecipesTable.path] = it.id.path
                    this[RecipesTable.type] = it.type.toString()
                    this[RecipesTable.input] = it.input.map { input -> input.export().joinToString() }
                    this[RecipesTable.output] = it.output.map { output -> output.export() }
                }
            }
        }
    }
}
