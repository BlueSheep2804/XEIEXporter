package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.ingredient.AbstractRecipeIngredient

interface IIngredientTranslator<T> {
    fun export(ingredient: Any): AbstractRecipeIngredient<T>?
}