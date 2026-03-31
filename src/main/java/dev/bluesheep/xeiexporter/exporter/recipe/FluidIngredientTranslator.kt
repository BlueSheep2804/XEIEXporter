package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.ingredient.AbstractRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.ingredient.FluidRecipeIngredient
import net.minecraftforge.fluids.FluidStack

class FluidIngredientTranslator : IIngredientTranslator<FluidStack> {
    override fun export(ingredient: Any): AbstractRecipeIngredient<FluidStack>? {
        if (FluidStack::class.isInstance(ingredient)) {
            return FluidRecipeIngredient(ingredient as FluidStack)
        }
        return null
    }
}