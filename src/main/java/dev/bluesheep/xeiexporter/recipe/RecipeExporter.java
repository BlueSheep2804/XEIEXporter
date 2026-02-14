package dev.bluesheep.xeiexporter.recipe;

import dev.bluesheep.xeiexporter.ExportUtil;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.List;

import static dev.bluesheep.xeiexporter.XEIExporter.EXPORT_DIR;

public class RecipeExporter {
    private static final Path EXPORT_RECIPES_DIR = EXPORT_DIR.resolve("recipes");
    private static final Path EXPORT_RECIPE_TYPE_FILE = EXPORT_RECIPES_DIR.resolve("recipe_types.json");

    private final ShapelessRecipeExporter shapelessRecipeExporter;

    public RecipeExporter() {
        shapelessRecipeExporter = new ShapelessRecipeExporter();
    }

    public void exportRecipes() {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        ExportUtil.mkdir(EXPORT_RECIPES_DIR);

        List<String> recipeTypes = ForgeRegistries.RECIPE_TYPES.getEntries().stream().map(recipeType -> recipeType.getKey().location().toString()).toList();
        ExportUtil.saveExportFile(recipeTypes, EXPORT_RECIPE_TYPE_FILE);

        level.getRecipeManager().getRecipes().forEach(recipe -> {
            JsonObject json;
            if (recipe instanceof ShapelessRecipe) {
                json = shapelessRecipeExporter.export((ShapelessRecipe) recipe, level);
            } else {
                return;
            }
            ExportUtil.saveExportFile(json, ExportUtil.resourceLocationToJson(EXPORT_RECIPES_DIR, recipe.getId()));
        });
    }
}
