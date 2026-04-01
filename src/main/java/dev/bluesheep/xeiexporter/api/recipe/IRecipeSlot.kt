package dev.bluesheep.xeiexporter.api.recipe

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import mezz.jei.api.ingredients.ITypedIngredient
import java.util.Optional

interface IRecipeSlot {
    fun export(): String
    fun exportList(): List<String>

    companion object {
        val EMPTY = object : IRecipeSlot {
            override fun export(): String {
                return ""
            }

            override fun exportList(): List<String> {
                return emptyList()
            }
        }

        fun createFrom(ingredientList: List<ITypedIngredient<*>>): IRecipeSlot {
            if (ingredientList.isEmpty()) return EMPTY

            val ingredientHelper = JEIExporterPlugin.runtime?.ingredientManager?.getIngredientHelper(ingredientList.first().ingredient)

            val tag = ingredientHelper?.getTagKeyEquivalent(ingredientList.map { it.ingredient }) ?: Optional.empty()
            return if (tag.isPresent) {
                val count = ingredientHelper?.getAmount(ingredientList.first().ingredient) ?: 1
                RecipeTagSlot(tag.get(), count)
            } else {
                RecipeSlot.fromTypedIngredients(ingredientList)
            }
        }
    }
}
