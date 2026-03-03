package dev.bluesheep.xeiexporter.exporter

import dev.bluesheep.xeiexporter.exporter.resources.ItemRendererExporter
import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.ItemsTable
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object ItemExporter {
    fun exportItems() {
        val itemsEntries = ForgeRegistries.ITEMS.getEntries()
        exportItem(itemsEntries)
    }

    private fun exportItem(entries: Set<Map.Entry<ResourceKey<Item>, Item>>) {
        transaction {
            DatabaseUtil.reset(ItemsTable)

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