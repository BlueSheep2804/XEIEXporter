package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.recipe.IRecipeStackData
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import kotlinx.serialization.Serializable
import mezz.jei.api.ingredients.IIngredientType
import mezz.jei.api.ingredients.ITypedIngredient

@Serializable
data class RecipeStackData(
    override val type: String,
    override val entry: String,
    override val amount: Int = 1,
    override val chance: Float = 1f
): IRecipeStackData {
    companion object {
        val EMPTY = RecipeStackData(
            "unknown",
            ExportUtil.rl("unknown").toString()
        )

        fun fromIngredient(typedIngredient: ITypedIngredient<*>): RecipeStackData {
            val ingredientManager = JEIExporterPlugin.Companion.runtime?.ingredientManager ?: return EMPTY
            val ingredient = typedIngredient.getIngredient(typedIngredient.type as IIngredientType<in Any>)
            if (ingredient.isEmpty) return EMPTY
            val helper = ingredientManager.getIngredientHelper(typedIngredient.ingredient)
            val amount = helper.getAmount(ingredient.get()).toInt()
            return RecipeStackData(
                helper.ingredientType.uid,
                helper.getResourceLocation(ingredient.get()).toString(),
                if (amount == -1) 1 else amount
            )
        }
    }
}