package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import mezz.jei.api.gui.IRecipeLayoutDrawable
import net.minecraft.resources.ResourceLocation

object JEIRecipeHandler {
    private val recipes: MutableMap<ResourceLocation, MutableMap<ResourceLocation, IRecipeLayoutDrawable<*>>> = mutableMapOf()

    @JvmStatic
    fun addRecipe(recipeId: ResourceLocation?, recipeLayout: IRecipeLayoutDrawable<*>) {
        val category = recipeLayout.recipeCategory
        val recipeTypeId = category.recipeType.uid

        if (!recipes.containsKey(recipeTypeId)) recipes.put(recipeTypeId, mutableMapOf())
        val categorizedRecipes = recipes[recipeTypeId]!!
        if (recipeId != null) {
            categorizedRecipes.put(recipeId, recipeLayout)
        } else {
            XEIExporter.LOGGER.info("No recipe id: $recipeTypeId[$recipeLayout]")
            categorizedRecipes.put(
                ExportUtil.rl("${recipeTypeId.toString().replace(':', '/')}/${categorizedRecipes.size + 1}"),
                recipeLayout
            )
        }
    }

    fun getRecipes(recipeType: ResourceLocation): Map<ResourceLocation, IRecipeLayoutDrawable<*>> {
        return recipes.getOrElse(recipeType) { emptyMap() }
    }
}