package dev.bluesheep.xeiexporter.api.recipe.ingredient

import dev.bluesheep.xeiexporter.exporter.ExportUtil
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries

class FluidRecipeIngredient(ingredient: FluidStack) : AbstractRecipeIngredient<FluidStack>(ingredient) {
    override val entryType: ResourceLocation = ExportUtil.rlVanilla("fluid")
    override val entry: ResourceLocation = ForgeRegistries.FLUIDS.getKey(ingredient.fluid) ?: ExportUtil.rl("unknown")
    override val amount: Int = ingredient.amount
    override val chance: Float = 1f

    override fun export(): String {
        return format(ForgeRegistries.FLUIDS.getKey(ingredient.fluid), ingredient.amount)
    }
}