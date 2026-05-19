package dev.bluesheep.xeiexporter.api

/**
 * Provides additional export metadata and ingredient discovery behavior for an ingredient type.
 *
 * Register implementations with `RegisterIngredientExtraHelperEvent` to customize how non-standard
 * ingredient types are named, enumerated, and serialized into recipe data.
 *
 * @param T ingredient stack or ingredient value type handled by this helper
 */
interface IIngredientExtraHelper<T> {
    /**
     * Complete ingredient collection to export for this type.
     *
     * @return the complete ingredient collection, or `null` to use the default list
     */
    val allIngredients: Collection<T>?
        get() = null

    /**
     * Extra ingredients appended to the resolved ingredient list.
     *
     * @return additional ingredients to export
     */
    val additionalIngredients: Collection<T>
        get() = emptyList()

    /**
     * Translation key used as the display name for this ingredient type.
     *
     * @return the ingredient type translation key, or `null` when unspecified
     */
    val ingredientNameKey: String?
        get() = null

    /**
     * Whether amounts for this ingredient type are represented in milli-units.
     *
     * This is typically `true` for fluid-like ingredients where `1000` represents one bucket.
     *
     * @return `true` if amounts are represented in milli-units, otherwise `false`
     */
    val isMilliUnit: Boolean
        get() = false

    /**
     * Returns the translation key or description id for an ingredient.
     *
     * @param ingredient ingredient to resolve
     * @return the ingredient translation key or description id, or `null` to use the default helper
     */
    fun getDescriptionId(ingredient: T): String? { return null }

    /**
     * Returns the exported amount for an ingredient stack.
     *
     * @param ingredient ingredient stack to inspect
     * @return the exported amount, or `null` to use the default helper amount
     */
    fun getAmount(ingredient: T): Long? { return null }
}
