package dev.bluesheep.xeiexporter.compat.mekanism

import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import mekanism.api.chemical.ChemicalStack

class ChemicalIngredientExtraHelper(override val ingredientNameKey: String?) : IIngredientExtraHelper<ChemicalStack<*>> {
    override val isMilliUnit: Boolean = true

    override fun getDescriptionId(ingredient: ChemicalStack<*>): String? {
        return ingredient.translationKey
    }

    override fun getAmount(ingredient: ChemicalStack<*>): Long? {
        return ingredient.amount
    }
}
