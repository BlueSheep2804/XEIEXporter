package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.IRecipeExporter
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.result.ItemRecipeResult
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rlVanilla
import net.minecraft.core.NonNullList
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraft.world.level.Level

class CraftingRecipeExporter() : IRecipeExporter<CraftingRecipe> {
    override val recipeTypeId: ResourceLocation = rlVanilla("crafting")
    override val inputSize: Int = 9
    override val outputSize: Int = 1

    override fun export(
        recipe: CraftingRecipe,
        level: Level
    ): RecipeData {
        val ingredients = NonNullList.withSize(9, Ingredient.EMPTY)
        when (recipe) {
            is ShapedRecipe -> {
                when (recipe.width) {
                    1 -> {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            ingredients[1 + index * 3] = ingredient
                        }
                    }
                    2 -> {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            ingredients[(index % 2) + (index / 2 * 3)] = ingredient
                        }
                    }
                    3 -> {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            ingredients[index] = ingredient
                        }
                    }
                }
            }
            is ShapelessRecipe -> {
                when (recipe.ingredients.size) {
                    1 -> {
                        ingredients[4] = recipe.ingredients.first()
                    }
                    in 2..4 -> {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            ingredients[(index % 2) + (index / 2 * 3)] = ingredient
                        }
                    }
                    else -> {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            ingredients[index] = ingredient
                        }
                    }
                }
            }
        }

        return RecipeData(
            recipe.id,
            recipeTypeId,
            ingredients.map { ItemRecipeIngredient(it) },
            listOf(ItemRecipeResult(recipe.getResultItem(level.registryAccess())))
        )
    }
}