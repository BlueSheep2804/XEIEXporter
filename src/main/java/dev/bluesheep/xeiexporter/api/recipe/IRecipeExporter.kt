package dev.bluesheep.xeiexporter.api.recipe

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.level.Level

interface IRecipeExporter<T : Recipe<out Container>> {
    val recipeTypeId: ResourceLocation

    fun export(recipe: T, level: Level): RecipeData
}