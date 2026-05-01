package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import net.minecraftforge.fluids.FluidStack

class FluidIngredientExtraHelper : IIngredientExtraHelper<FluidStack> {
    override fun getDescriptionId(ingredient: FluidStack): String? {
        return ingredient.translationKey
    }
}