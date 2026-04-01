package dev.bluesheep.xeiexporter.api.recipe

import dev.bluesheep.xeiexporter.api.recipe.ingredient.AbstractRecipeIngredient
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeIngredient
import mezz.jei.api.ingredients.ITypedIngredient

class RecipeSlot(val ingredients: List<AbstractRecipeIngredient<*>>): IRecipeSlot {
    companion object {
        fun fromTypedIngredients(ingredientList: List<ITypedIngredient<*>>): RecipeSlot {
            return RecipeSlot(
                ingredientList.map {
                    RecipeIngredient.getIngredient(it)
                }
            )
        }
    }

    override fun export(): String {
        return ingredients.joinToString(",") { it.export() }
    }

    override fun exportList(): List<String> {
        return ingredients.map {
            it.export()
        }
    }
}