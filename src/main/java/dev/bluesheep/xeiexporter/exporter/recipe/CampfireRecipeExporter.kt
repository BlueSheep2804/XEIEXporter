package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.IRecipeExporter
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.result.ItemRecipeResult
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlVanilla
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.CampfireCookingRecipe
import net.minecraft.world.level.Level

class CampfireRecipeExporter : IRecipeExporter<CampfireCookingRecipe> {
    override val recipeTypeId: ResourceLocation = rlVanilla("campfire_cooking")
    override val inputSize: Int = 1
    override val outputSize: Int = 1

    override fun export(
        recipe: CampfireCookingRecipe,
        level: Level
    ): RecipeData {
        return RecipeData(
            recipe.id,
            recipeTypeId,
            recipe.ingredients.map { ItemRecipeIngredient(it) },
            listOf(ItemRecipeResult(recipe.getResultItem(level.registryAccess())))
        )
    }
}