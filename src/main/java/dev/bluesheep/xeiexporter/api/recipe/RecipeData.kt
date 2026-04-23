package dev.bluesheep.xeiexporter.api.recipe

import net.minecraft.resources.ResourceLocation

data class RecipeData(
    val id: ResourceLocation,
    val type: ResourceLocation,
    val input: List<List<RecipeStackData>>,
    val output: List<List<RecipeStackData>>
)