package dev.bluesheep.xeiexporter

import com.google.gson.JsonObject
import dev.bluesheep.xeiexporter.resources.ItemRendererExporter
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import java.nio.file.Path
import java.util.function.Consumer

object ItemExporter {
    private val EXPORT_ITEM_FILE: Path = XEIExporter.EXPORT_DIR.resolve("items.json")
    private val EXPORT_ITEMS_DIR: Path = XEIExporter.EXPORT_DIR.resolve("items")

    fun exportItems() {
        val itemsEntries = ForgeRegistries.ITEMS.getEntries()
        ExportUtil.mkdir(EXPORT_ITEMS_DIR)
//        itemsEntries.forEach(Consumer { obj: MutableMap.MutableEntry<ResourceKey<Item?>?, Item?>? -> ItemExporter.exportItem() })
        itemsEntries.forEach(::exportItem)

        val itemRendererExporter = ItemRendererExporter()
//        itemsEntries.forEach(Consumer { itemEntry: MutableMap.MutableEntry<ResourceKey<Item?>?, Item?>? ->
//            itemRendererExporter.addItem(
//                itemEntry
//            )
//        })
        itemsEntries.forEach(itemRendererExporter::addItem)
        itemRendererExporter.end()

//        val itemList = itemsEntries.map { it: MutableMap.MutableEntry<ResourceKey<Item?>?, Item?>? ->
//            it!!.key!!.location().toString()
//        }.toList()
        val itemList = itemsEntries.map { (key, _) -> key.location().toString() }
        ExportUtil.saveExportFile(itemList, EXPORT_ITEM_FILE)
    }

    private fun exportItem(itemEntry: MutableMap.MutableEntry<ResourceKey<Item>, Item>) {
        val item = itemEntry.value
        val itemId = itemEntry.key.location()
        val stack = ItemStack(item)

        val json = JsonObject()
        json.addProperty("id", itemId.toString())
        json.addProperty("description_id", item.descriptionId)
        json.addProperty("max_damage", item.getMaxDamage(stack))
        json.addProperty("rarity", item.getRarity(stack).name.lowercase())
        json.addProperty("class", item.javaClass.name)

        ExportUtil.saveExportFile(json, ExportUtil.resourceLocationToJson(EXPORT_ITEMS_DIR, itemId))
    }
}
