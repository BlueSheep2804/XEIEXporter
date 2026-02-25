package dev.bluesheep.xeiexporter.api.recipe.result

abstract class AbstractRecipeResult<T>(val result: T) {
    abstract fun export(): String
}