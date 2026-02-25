package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.IRecipeExporter
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.result.ItemRecipeResult
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.AbstractCookingRecipe
import net.minecraft.world.level.Level

class CookingRecipeExporter<T : AbstractCookingRecipe>(override val recipeTypeId: ResourceLocation) : IRecipeExporter<T> {
    override fun export(recipe: T, level: Level): RecipeData {
        val inputs = recipe.ingredients.map { ItemRecipeIngredient(it) }
        val result = ItemRecipeResult(recipe.getResultItem(level.registryAccess()))

        return RecipeData(
            recipe.id,
            recipeTypeId,
            inputs,
            listOf(result)
        )
    }
}