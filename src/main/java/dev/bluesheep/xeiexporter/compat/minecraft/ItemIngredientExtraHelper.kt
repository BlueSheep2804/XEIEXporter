package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import net.minecraft.world.item.ItemStack

class ItemIngredientExtraHelper : IIngredientExtraHelper<ItemStack> {
    override val ingredientNameKey: String? = "ingredient.minecraft.item_stack"

    override fun getDescriptionId(ingredient: ItemStack): String? {
        return ingredient.descriptionId
    }
}