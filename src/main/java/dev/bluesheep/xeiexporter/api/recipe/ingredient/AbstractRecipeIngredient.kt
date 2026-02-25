package dev.bluesheep.xeiexporter.api.recipe.ingredient

abstract class AbstractRecipeIngredient<T>(val ingredients: List<T>) {
    abstract fun export(): List<String>
}