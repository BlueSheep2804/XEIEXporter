package dev.bluesheep.xeiexporter.api.recipe

import net.minecraft.resources.ResourceLocation

data class RecipeData(
    val id: ResourceLocation,
    val type: ResourceLocation,
    val input: List<RecipeSlot>,
    val output: List<RecipeSlot>
)