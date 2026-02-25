package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.IRecipeExporter
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.result.ItemRecipeResult
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlVanilla
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.level.Level

class CraftingRecipeExporter() : IRecipeExporter<CraftingRecipe> {
    override val recipeTypeId: ResourceLocation = rlVanilla("crafting")
    override val inputSize: Int = 9
    override val outputSize: Int = 1

    override fun export(
        recipe: CraftingRecipe,
        level: Level
    ): RecipeData {
        val ingredients = recipe.ingredients.map { ItemRecipeIngredient(it) }
        return RecipeData(
            recipe.id,
            recipeTypeId,
            ingredients,
            listOf(ItemRecipeResult(recipe.getResultItem(level.registryAccess())))
        )
    }
}