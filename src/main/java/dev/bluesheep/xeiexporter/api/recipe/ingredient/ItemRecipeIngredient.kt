package dev.bluesheep.xeiexporter.api.recipe.ingredient

import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

class ItemRecipeIngredient(ingredient: ItemStack) : AbstractRecipeIngredient<ItemStack>(ingredient) {
    override fun export(): String {
        val count = if (ingredient.count > 1) "${ingredient.count}x " else ""
        return "${count}${ForgeRegistries.ITEMS.getKey(ingredient.item)}"
    }
}