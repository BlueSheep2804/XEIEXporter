package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import net.minecraft.world.item.ItemStack

class ItemIngredientExtraHelper : IIngredientExtraHelper<ItemStack> {
    override fun getDescriptionId(ingredient: ItemStack): String? {
        return ingredient.descriptionId
    }
}