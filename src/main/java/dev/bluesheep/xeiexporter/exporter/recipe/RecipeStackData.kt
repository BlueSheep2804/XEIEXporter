package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import dev.bluesheep.xeiexporter.api.recipe.IRecipeStackData
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import dev.bluesheep.xeiexporter.exporter.ingredient.IngredientExporter
import kotlinx.serialization.Serializable
import mezz.jei.api.ingredients.IIngredientType
import mezz.jei.api.ingredients.ITypedIngredient
import mezz.jei.api.ingredients.subtypes.UidContext

@Serializable
data class RecipeStackData(
    override val type: String,
    override val entry: String,
    override val uniqueId: String = "",
    override val amount: Long = 1,
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

            val helper = ingredientManager.getIngredientHelper(ingredient.get())
            val extraHelper = IngredientExporter.extraHelpers[typedIngredient.type.ingredientClass] as IIngredientExtraHelper<Any>?
            val amount = extraHelper?.getAmount(ingredient.get()) ?: helper.getAmount(ingredient.get())

            val uniqueId = if (helper.hasSubtypes(typedIngredient.ingredient)) {
                helper.getUniqueId(ingredient.get(),UidContext.Ingredient)
            } else {
                ""
            }
            return RecipeStackData(
                helper.ingredientType.uid,
                helper.getResourceLocation(ingredient.get()).toString(),
                uniqueId,
                if (amount == -1L) 1 else amount
            )
        }
    }
}