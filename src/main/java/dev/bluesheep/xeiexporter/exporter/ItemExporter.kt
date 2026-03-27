package dev.bluesheep.xeiexporter.exporter

import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.ItemsTable
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object ItemExporter {
    fun exportItems() {
        val itemsEntries = ForgeRegistries.ITEMS.entries

        transaction {
            DatabaseUtil.reset(ItemsTable)

            ItemsTable.batchInsert(itemsEntries) { itemEntry ->
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