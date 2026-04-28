package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.api.IIngredientModifier
import net.minecraftforge.fluids.FluidStack

class FluidIngredientModifier : IIngredientModifier<FluidStack> {
    override fun mapDescriptionId(ingredient: FluidStack): String? {
        return ingredient.translationKey
    }
}