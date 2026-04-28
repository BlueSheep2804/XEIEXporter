package dev.bluesheep.xeiexporter.compat.minecraft

import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.api.event.RegisterIngredientModifierEvent
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.forge.ForgeTypes
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus

@EventBusSubscriber(modid = XEIExporter.MODID, bus = Bus.MOD)
object EventHandler {
    @SubscribeEvent
    fun registerIngredientModifier(event: RegisterIngredientModifierEvent) {
        event.register(VanillaTypes.ITEM_STACK, ItemIngredientModifier())
        event.register(ForgeTypes.FLUID_STACK, FluidIngredientModifier())
    }
}