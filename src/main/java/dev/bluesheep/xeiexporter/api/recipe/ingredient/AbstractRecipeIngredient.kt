package dev.bluesheep.xeiexporter.api.recipe.ingredient

import dev.bluesheep.xeiexporter.exporter.ExportUtil
import net.minecraft.resources.ResourceLocation

abstract class AbstractRecipeIngredient<T>(val ingredient: T) {
    companion object {
        val UNKNOWN_ENTRY = ExportUtil.rl("unknown")
    }
    abstract val entryType: ResourceLocation
    abstract val entry: ResourceLocation
    abstract val amount: Int
    abstract val chance: Float
    abstract fun export(): String

    protected fun format(entryLocation: ResourceLocation?, count: Int = 1, hideSingularAmount: Boolean = true): String {
        val entry = "${entryType};${entryLocation ?: UNKNOWN_ENTRY}"
        return if (count == 1 && hideSingularAmount) {
            entry
        } else {
            "${count}x $entry"
        }
    }
}