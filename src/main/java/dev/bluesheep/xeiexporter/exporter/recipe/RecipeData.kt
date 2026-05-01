package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.IRecipeData
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeStackData
import net.minecraft.resources.ResourceLocation

data class RecipeData(
    override val id: ResourceLocation,
    override val type: ResourceLocation,
    override val input: List<List<RecipeStackData>>,
    override val output: List<List<RecipeStackData>>
) : IRecipeData