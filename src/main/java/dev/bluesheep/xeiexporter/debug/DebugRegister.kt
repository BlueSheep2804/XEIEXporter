package dev.bluesheep.xeiexporter.debug

import dev.bluesheep.xeiexporter.XEIExporter
import net.minecraft.core.registries.Registries
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer
import java.util.function.Supplier

object DebugRegister {
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, XEIExporter.MODID)
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, XEIExporter.MODID)
    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, XEIExporter.MODID)

    val EXAMPLE_BLOCK: RegistryObject<Block> = BLOCKS.register(
        "example_block",
        Supplier { Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)) })
    val EXAMPLE_BLOCK_ITEM: RegistryObject<Item> =
        ITEMS.register("example_block", Supplier { BlockItem(EXAMPLE_BLOCK.get(), Item.Properties()) })

    val EXAMPLE_ITEM: RegistryObject<Item?> = ITEMS.register("example_item", Supplier {
        Item(
            Item.Properties().food(
                FoodProperties.Builder()
                    .alwaysEat().nutrition(1).saturationMod(2f).build()
            )
        )
    })

    val EXAMPLE_TAB: RegistryObject<CreativeModeTab> =
        CREATIVE_MODE_TABS.register("example_tab", Supplier {
            CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon { EXAMPLE_ITEM.get().defaultInstance }
                .displayItems { parameters: ItemDisplayParameters, output: CreativeModeTab.Output ->
                    ITEMS.getEntries().forEach(Consumer { itemRegistryObject: RegistryObject<Item> ->
                        output.accept(itemRegistryObject.get())
                    }
                    )
                }.build()
        })
}
