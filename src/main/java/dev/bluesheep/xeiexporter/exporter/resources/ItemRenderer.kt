package dev.bluesheep.xeiexporter.exporter.resources

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

class ItemRenderer : AbstractRenderSystemRenderer() {
    override val name: String = "items"

    override val renderList: Map<ResourceLocation, (GuiGraphics) -> Unit>
        get() = ForgeRegistries.ITEMS.entries.associate { (key, item) ->
            val itemStack = ItemStack(item)
            return@associate key.location() to { guiGraphics: GuiGraphics -> guiGraphics.renderItem(itemStack, 0, 0) }
        }
}