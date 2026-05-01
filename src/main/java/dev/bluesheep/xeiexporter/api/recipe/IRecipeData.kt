package dev.bluesheep.xeiexporter.api.recipe

import dev.bluesheep.xeiexporter.exporter.recipe.RecipeStackData
import net.minecraft.resources.ResourceLocation

interface IRecipeData {
    val id: ResourceLocation
    val type: ResourceLocation
    val input: List<List<RecipeStackData>>
    val output: List<List<RecipeStackData>>
}