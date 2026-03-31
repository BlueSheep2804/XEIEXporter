package dev.bluesheep.xeiexporter.api.recipe.ingredient

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries

class FluidRecipeIngredient(ingredient: FluidStack) : AbstractRecipeIngredient<FluidStack>(ingredient) {
    override fun export(): String {
        val count = ingredient.amount
        return "${count}x ${ForgeRegistries.FLUIDS.getKey(ingredient.fluid)}"
    }
}