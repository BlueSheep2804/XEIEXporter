package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.exporter.ExportUtil.mkdir
import dev.bluesheep.xeiexporter.exporter.ExportUtil.resourceLocationToJson
import dev.bluesheep.xeiexporter.exporter.ExportUtil.saveExportFile
import dev.bluesheep.xeiexporter.XEIExporter
import net.minecraft.client.Minecraft
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraftforge.registries.ForgeRegistries
import java.nio.file.Path

class RecipeExporter {
    companion object {
        private val EXPORT_RECIPES_DIR: Path = XEIExporter.EXPORT_DIR.resolve("recipes")
        private val EXPORT_RECIPE_TYPE_FILE: Path = EXPORT_RECIPES_DIR.resolve("recipe_types.json")
    }

    private val shapelessRecipeExporter = ShapelessRecipeExporter()

    fun exportRecipes() {
        val level = Minecraft.getInstance().level ?: return

        mkdir(EXPORT_RECIPES_DIR)

        val recipeTypes = ForgeRegistries.RECIPE_TYPES.entries.map { (key, _) -> key.location().toString() }
        saveExportFile(recipeTypes, EXPORT_RECIPE_TYPE_FILE)

        level.recipeManager.recipes.forEach {
            val json = if (it is ShapelessRecipe) {
                shapelessRecipeExporter.export(it, level)
            } else {
                return@forEach
            }
            saveExportFile(json, resourceLocationToJson(EXPORT_RECIPES_DIR, it.id))
        }
    }
}
