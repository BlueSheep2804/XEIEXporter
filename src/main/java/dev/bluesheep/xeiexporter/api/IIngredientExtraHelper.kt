package dev.bluesheep.xeiexporter.api

interface IIngredientExtraHelper<T> {
    val allIngredients: Collection<T>?
        get() = null

    val additionalIngredients: Collection<T>
        get() = emptyList()

    val ingredientNameKey: String?
        get() = null

    val isMilliUnit: Boolean
        get() = false

    fun getDescriptionId(ingredient: T): String? { return null }

    fun getAmount(ingredient: T): Long? { return null }
}