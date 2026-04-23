package dev.bluesheep.xeiexporter.api.recipe

import dev.bluesheep.xeiexporter.api.recipe.ingredient.AbstractRecipeIngredient
import kotlinx.serialization.Serializable

@Serializable
data class RecipeStackData(
    val type: String,
    val entry: String,
    val amount: Int = 1,
    val chance: Float = 1f
) {
    companion object {
        fun fromIngredient(ingredient: AbstractRecipeIngredient<*>): RecipeStackData {
            return RecipeStackData(
                ingredient.entryType.toString(),
                ingredient.entry.toString(),
                ingredient.amount,
                ingredient.chance
            )
        }
    }
}
