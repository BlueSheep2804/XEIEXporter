package dev.bluesheep.xeiexporter.api.recipe.result

import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

class ItemRecipeResult(result: ItemStack) : AbstractRecipeResult<ItemStack>(result) {
    override fun export(): String {
        val count = if (result.count > 1) "${result.count}x " else ""
        return "${count}${ForgeRegistries.ITEMS.getKey(result.item)}"
    }
}