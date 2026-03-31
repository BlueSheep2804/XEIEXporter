package dev.bluesheep.xeiexporter.api.recipe

import dev.bluesheep.xeiexporter.api.recipe.ingredient.AbstractRecipeIngredient

class RecipeSlot(ingredients: List<AbstractRecipeIngredient<*>>) {
    private val ingredients = ingredients.toMutableList()

    fun export(): String {
        return ingredients.joinToString(",") { it.export() }
    }

    fun exportList(): List<String> {
        return ingredients.map {
            it.export()
        }
    }
}