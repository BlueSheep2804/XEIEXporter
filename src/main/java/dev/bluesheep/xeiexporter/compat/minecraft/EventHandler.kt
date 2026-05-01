package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.api.event.RegisterIngredientExtraHelperEvent
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
    fun registerIngredientExtraHelper(event: RegisterIngredientExtraHelperEvent) {
        event.register(VanillaTypes.ITEM_STACK, ItemIngredientExtraHelper())
        event.register(ForgeTypes.FLUID_STACK, FluidIngredientExtraHelper())
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