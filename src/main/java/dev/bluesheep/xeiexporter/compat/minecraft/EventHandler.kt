package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.api.event.RegisterIngredientModifierEvent
import dev.bluesheep.xeiexporter.api.event.RegisterTagEvent
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.forge.ForgeTypes
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus
import net.minecraftforge.registries.ForgeRegistries

@EventBusSubscriber(modid = XEIExporter.MODID, bus = Bus.MOD)
object EventHandler {
    @SubscribeEvent
    fun registerIngredientModifier(event: RegisterIngredientModifierEvent) {
        event.register(VanillaTypes.ITEM_STACK, ItemIngredientModifier())
        event.register(ForgeTypes.FLUID_STACK, FluidIngredientModifier())
    }

    @SubscribeEvent
    fun registerTag(event: RegisterTagEvent) {
        event.register(VanillaTypes.ITEM_STACK) {
            return@register ForgeRegistries.ITEMS.tags()?.associate {
                it.key.location to it.map { item -> ForgeRegistries.ITEMS.getKey(item) ?: ExportUtil.UNKNOWN }
            } ?: emptyMap()
        }
        event.register(ForgeTypes.FLUID_STACK) {
            return@register ForgeRegistries.FLUIDS.tags()?.associate {
                it.key.location to it.map { fluid -> ForgeRegistries.FLUIDS.getKey(fluid) ?: ExportUtil.UNKNOWN }
            } ?: emptyMap()
        }
    }
}