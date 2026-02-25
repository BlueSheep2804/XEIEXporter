package dev.bluesheep.xeiexporter.api.recipe

import dev.bluesheep.xeiexporter.api.recipe.ingredient.AbstractRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.result.AbstractRecipeResult
import net.minecraft.resources.ResourceLocation

data class RecipeData(
    val id: ResourceLocation,
    val type: ResourceLocation,
    val input: List<AbstractRecipeIngredient<*>>,
    val output: List<AbstractRecipeResult<*>>
)