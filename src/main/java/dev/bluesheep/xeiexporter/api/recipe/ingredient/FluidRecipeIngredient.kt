package dev.bluesheep.xeiexporter.api.recipe.ingredient

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries

class FluidRecipeIngredient(ingredient: FluidStack) : AbstractRecipeIngredient<FluidStack>(ingredient) {
    override val entryType: String = "fluid"

    override fun export(): String {
        return format(ForgeRegistries.FLUIDS.getKey(ingredient.fluid), ingredient.amount)
    }
}