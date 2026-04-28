package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.api.IIngredientModifier
import net.minecraft.world.item.ItemStack

class ItemIngredientModifier : IIngredientModifier<ItemStack> {
    override fun mapDescriptionId(ingredient: ItemStack): String? {
        return ingredient.descriptionId
    }
}