package dev.bluesheep.xeiexporter.api

interface IIngredientModifier<T> {
    val allIngredients: Collection<T>?
        get() = null

    val additionalIngredients: Collection<T>
        get() = emptyList()

    fun mapDescriptionId(ingredient: T): String? { return null }
}