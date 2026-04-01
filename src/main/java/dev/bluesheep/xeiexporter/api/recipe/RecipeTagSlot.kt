package dev.bluesheep.xeiexporter.api.recipe

import net.minecraft.tags.TagKey

class RecipeTagSlot<T>(val tag: TagKey<T>, val count: Long): IRecipeSlot {
    override fun export(): String {
        val count = if (count > 1) "${count}x " else ""
        return "$count#${tag.location}"
    }

    override fun exportList(): List<String> {
        return listOf(export())
    }
}