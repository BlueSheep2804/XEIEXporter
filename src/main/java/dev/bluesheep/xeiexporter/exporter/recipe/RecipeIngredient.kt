package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.ingredient.AbstractRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.ingredient.EmptyRecipeIngredient
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.forge.ForgeTypes
import mezz.jei.api.ingredients.IIngredientType
import mezz.jei.api.ingredients.ITypedIngredient

object RecipeIngredient {
    private val translators = mapOf<IIngredientType<*>, IIngredientTranslator<*>>(
        VanillaTypes.ITEM_STACK to ItemIngredientTranslator(),
        ForgeTypes.FLUID_STACK to FluidIngredientTranslator()
    )

    fun getIngredient(typedIngredient: ITypedIngredient<*>): AbstractRecipeIngredient<*> {
        return translators[typedIngredient.type]?.export(typedIngredient.ingredient) ?: EmptyRecipeIngredient()
    }

    fun getIngredient(ingredient: Any): AbstractRecipeIngredient<*> {
        val keyResult = translators.filterKeys { it.ingredientClass == ingredient::class.java }
        return if (keyResult.isNotEmpty()) {
            translators[keyResult.keys.first()]?.export(ingredient) ?: EmptyRecipeIngredient()
        } else {
            EmptyRecipeIngredient()
        }
    }
}