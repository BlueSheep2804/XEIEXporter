package dev.bluesheep.xeiexporter.api.recipe.ingredient

abstract class AbstractRecipeIngredient<T>(val ingredient: T) {
    abstract fun export(): String
}