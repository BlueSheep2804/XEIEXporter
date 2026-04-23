package dev.bluesheep.xeiexporter.api.recipe.ingredient

import dev.bluesheep.xeiexporter.exporter.ExportUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

class ItemRecipeIngredient(ingredient: ItemStack) : AbstractRecipeIngredient<ItemStack>(ingredient) {
    override val entryType: ResourceLocation = ExportUtil.rlVanilla("item")
    override val entry: ResourceLocation = ForgeRegistries.ITEMS.getKey(ingredient.item) ?: ExportUtil.rl("unknown")
    override val amount: Int = ingredient.count
    override val chance: Float = 1f

    override fun export(): String {
        return format(ForgeRegistries.ITEMS.getKey(ingredient.item), ingredient.count)
    }
}