package dev.bluesheep.xeiexporter.exporter

import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.exporter.resources.ItemRendererExporter
import dev.bluesheep.xeiexporter.sql.ItemsTable
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.exists
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.nio.file.Path

object ItemExporter {
    private val EXPORT_ITEM_FILE: Path = XEIExporter.EXPORT_DIR.resolve("items.json")
    private val EXPORT_ITEMS_DIR: Path = XEIExporter.EXPORT_DIR.resolve("items")

    fun exportItems() {
        val itemsEntries = ForgeRegistries.ITEMS.getEntries()
        exportItem(itemsEntries)

        val itemRendererExporter = ItemRendererExporter()
        itemsEntries.forEach(itemRendererExporter::addItem)
        itemRendererExporter.end()

//        val itemList = itemsEntries.map { (key, _) -> key.location().toString() }
//        ExportUtil.saveExportFile(itemList, EXPORT_ITEM_FILE)
    }

//    private fun exportItem(itemEntry: MutableMap.MutableEntry<ResourceKey<Item>, Item>) {
//        val item = itemEntry.value
//        val itemId = itemEntry.key.location()
//        val stack = ItemStack(item)
//
//        val json = JsonObject()
//        json.addProperty("id", itemId.toString())
//        json.addProperty("description_id", item.descriptionId)
//        json.addProperty("max_damage", item.getMaxDamage(stack))
//        json.addProperty("rarity", item.getRarity(stack).name.lowercase())
//        json.addProperty("class", item.javaClass.name)
//
//        ExportUtil.saveExportFile(json, ExportUtil.resourceLocationToJson(EXPORT_ITEMS_DIR, itemId))
//    }

    private fun exportItem(entries: Set<Map.Entry<ResourceKey<Item>, Item>>) {
        transaction {
            if (!ItemsTable.exists()) {
                SchemaUtils.create(ItemsTable)
            } else {
//                ItemsTable.deleteAll()
                SchemaUtils.drop(ItemsTable)
                SchemaUtils.create(ItemsTable)
            }
            ItemsTable.batchInsert(entries) { itemEntry ->
                val itemId = itemEntry.key.location()
                val itemStack = ItemStack(itemEntry.value)
                this[ItemsTable.namespace] = itemId.namespace
                this[ItemsTable.name] = itemId.path
                this[ItemsTable.descriptionId] = itemStack.descriptionId
                this[ItemsTable.maxDamage] = itemStack.maxDamage
                this[ItemsTable.rarity] = itemStack.rarity
                this[ItemsTable.clazz] = itemStack.item.javaClass.name
            }
        }
    }
}