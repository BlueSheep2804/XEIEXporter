package dev.bluesheep.xeiexporter.debug

import dev.bluesheep.xeiexporter.XEIExporter
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object DebugRegister {
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, XEIExporter.MODID)
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, XEIExporter.MODID)
    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, XEIExporter.MODID)

    val SIMPLE_BLOCK: RegistryObject<Block> = BLOCKS.register("simple_block") {
        Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
    }
    val SIMPLE_BLOCK_ITEM: RegistryObject<Item> = ITEMS.register("simple_block") {
        BlockItem(SIMPLE_BLOCK.get(), Item.Properties())
    }

    val DEBUG_FOOD: RegistryObject<Item> = ITEMS.register("debug_food") {
        Item(
            Item.Properties().food(
                FoodProperties.Builder()
                    .alwaysEat()
                    .nutrition(1)
                    .saturationMod(2f)
                    .effect({
                        MobEffectInstance(MobEffects.GLOWING, 30)
                    }, 30f)
                    .build()
            )
        )
    }

    val DEBUG_TAB: RegistryObject<CreativeModeTab> =
        CREATIVE_MODE_TABS.register("debug_tab") {
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.xeiexporter.debug_tab"))
                .icon { DEBUG_FOOD.get().defaultInstance }
                .displayItems { parameters: ItemDisplayParameters, output: CreativeModeTab.Output ->
                    ITEMS.getEntries().forEach {
                        output.accept(it.get())
                    }
                }.build()
        }
}
