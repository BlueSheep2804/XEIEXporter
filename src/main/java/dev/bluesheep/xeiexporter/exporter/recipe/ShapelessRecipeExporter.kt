package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.IRecipeExporter
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.result.ItemRecipeResult
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlVanilla
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraft.world.level.Level

class ShapelessRecipeExporter() : IRecipeExporter<ShapelessRecipe> {
    override val recipeTypeId: ResourceLocation = rlVanilla("crafting_shapeless")

    override fun export(recipe: ShapelessRecipe, level: Level): RecipeData {
        val ingredients = recipe.ingredients.map { ItemRecipeIngredient(it) }
        return RecipeData(
            recipe.id,
            recipeTypeId,
            ingredients,
            listOf(ItemRecipeResult(recipe.getResultItem(level.registryAccess())))
        )
    }
}
