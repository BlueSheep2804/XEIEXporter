package dev.bluesheep.xeiexporter.api.recipe

import net.minecraft.resources.ResourceLocation

interface IRecipeData {
    val id: ResourceLocation
    val type: ResourceLocation
    val input: List<List<IRecipeStackData>>
    val output: List<List<IRecipeStackData>>
}