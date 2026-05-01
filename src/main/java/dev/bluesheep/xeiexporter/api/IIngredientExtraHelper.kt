package dev.bluesheep.xeiexporter.api

interface IIngredientExtraHelper<T> {
    val allIngredients: Collection<T>?
        get() = null

    val additionalIngredients: Collection<T>
        get() = emptyList()

    fun getDescriptionId(ingredient: T): String? { return null }

    fun getAmount(ingredient: T): Long? { return null }
}