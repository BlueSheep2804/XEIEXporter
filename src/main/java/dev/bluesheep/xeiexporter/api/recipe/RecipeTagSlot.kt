package dev.bluesheep.xeiexporter.api.recipe

import net.minecraft.tags.TagKey

class RecipeTagSlot<T>(val tag: TagKey<T>, val count: Long): IRecipeSlot {
    override fun export(): String {
        val tagRegistry = tag.registry.location().let {
            if (it.namespace == "minecraft") {
                it.path
            } else {
                it.toString()
            }
        }
        val entry = "#${tagRegistry};${tag.location}"
        return if (count == 1L) {
            entry
        } else {
            "${count}x $entry"
        }
    }

    override fun exportList(): List<String> {
        return listOf(export())
    }
}