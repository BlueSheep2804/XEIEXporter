package dev.bluesheep.xeiexporter.api.recipe.ingredient

import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

class ItemRecipeIngredient(ingredient: ItemStack) : AbstractRecipeIngredient<ItemStack>(ingredient) {
    override val entryType: String = "item"

    override fun export(): String {
        return format(ForgeRegistries.ITEMS.getKey(ingredient.item), ingredient.count)
    }
}