package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.recipe.RecipeStackData
import mezz.jei.api.ingredients.ITypedIngredient
import java.util.Optional

object RecipeSlot {
    fun createFrom(ingredientList: List<ITypedIngredient<*>>): List<RecipeStackData> {
        if (ingredientList.isEmpty()) return emptyList()

        if (ingredientList.size > 1) {
            val ingredientHelper = JEIExporterPlugin.runtime?.ingredientManager?.getIngredientHelper(ingredientList.first().ingredient)

            val tag = ingredientHelper?.getTagKeyEquivalent(ingredientList.map { it.ingredient }) ?: Optional.empty()
            if (tag.isPresent) {
                val first = RecipeIngredient.getIngredient(ingredientList.first())
                val firstAmount = ingredientHelper?.getAmount(ingredientList.first().ingredient) ?: 0
                val allCountsMatch = ingredientList.all {
                    firstAmount == (ingredientHelper?.getAmount(it.ingredient) ?: 0)
                }

                if (allCountsMatch) {
                    return listOf(
                        RecipeStackData(
                            first.entryType.toString(),
                            "#${tag.get().location}",
                            firstAmount.toInt(),
                            1f
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
                RecipeIngredient.getIngredient(it)
            )
        }
    }
}
