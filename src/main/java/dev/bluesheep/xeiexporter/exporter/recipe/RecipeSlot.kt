package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import mezz.jei.api.ingredients.ITypedIngredient
import java.util.Optional

object RecipeSlot {
    fun createFrom(ingredientList: List<ITypedIngredient<*>>): List<RecipeStackData> {
        if (ingredientList.isEmpty()) return emptyList()

        if (ingredientList.size > 1) {
            val ingredientHelper = JEIExporterPlugin.runtime?.ingredientManager?.getIngredientHelper(ingredientList.first().ingredient)

            val tag = ingredientHelper?.getTagKeyEquivalent(ingredientList.map { it.ingredient }) ?: Optional.empty()
            if (tag.isPresent) {
                val first = RecipeStackData.fromIngredient(ingredientList.first())
                val allCountsMatch = ingredientList.all {
                    first.amount == (ingredientHelper?.getAmount(it.ingredient)?.toInt() ?: 0)
                }

                if (allCountsMatch) {
                    return listOf(
                        RecipeStackData(
                            first.type,
                            "#${tag.get().location}",
                            amount = first.amount
                        )
                    )
                }
            }
        }
        return fromTypedIngredients(ingredientList)
    }

    fun fromTypedIngredients(ingredientList: List<ITypedIngredient<*>>): List<RecipeStackData> {
        return ingredientList.map {
            RecipeStackData.fromIngredient(
                it
            )
        }
    }
}
