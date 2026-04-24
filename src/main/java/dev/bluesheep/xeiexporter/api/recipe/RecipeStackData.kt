package dev.bluesheep.xeiexporter.api.recipe

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import kotlinx.serialization.Serializable
import mezz.jei.api.ingredients.IIngredientType
import mezz.jei.api.ingredients.ITypedIngredient

@Serializable
data class RecipeStackData(
    val type: String,
    val entry: String,
    val amount: Int = 1,
    val chance: Float = 1f
) {
    companion object {
        val EMPTY = RecipeStackData(
            ExportUtil.rl("unknown").toString(),
            ExportUtil.rl("unknown").toString()
        )

        fun fromIngredient(typedIngredient: ITypedIngredient<*>): RecipeStackData {
            val ingredientManager = JEIExporterPlugin.runtime?.ingredientManager ?: return EMPTY
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
