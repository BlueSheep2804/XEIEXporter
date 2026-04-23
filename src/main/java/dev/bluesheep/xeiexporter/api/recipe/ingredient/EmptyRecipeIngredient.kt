package dev.bluesheep.xeiexporter.api.recipe.ingredient

import dev.bluesheep.xeiexporter.exporter.ExportUtil
import net.minecraft.resources.ResourceLocation

class EmptyRecipeIngredient : AbstractRecipeIngredient<String>("") {
    override val entryType: ResourceLocation = ExportUtil.rl("unknown")
    override val entry: ResourceLocation = ExportUtil.rl("unknown")
    override val amount: Int = 1
    override val chance: Float = 1f

    override fun export(): String {
        return ""
    }
}