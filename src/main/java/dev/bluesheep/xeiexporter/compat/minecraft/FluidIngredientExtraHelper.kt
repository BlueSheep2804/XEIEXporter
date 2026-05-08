package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import net.minecraftforge.fluids.FluidStack

class FluidIngredientExtraHelper : IIngredientExtraHelper<FluidStack> {
    override val ingredientNameKey: String? = "ingredient.minecraft.fluid_stack"
    override val isMilliUnit: Boolean = true

    override fun getDescriptionId(ingredient: FluidStack): String? {
        return ingredient.translationKey
    }
}