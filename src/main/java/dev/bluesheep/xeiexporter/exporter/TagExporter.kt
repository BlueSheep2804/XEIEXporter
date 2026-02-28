package dev.bluesheep.xeiexporter.exporter

import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.ItemTagsTable
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class TagExporter {
    fun export() {
        Minecraft.getInstance().level?.registryAccess()?.registries()
        val itemTags = ForgeRegistries.ITEMS.tags()?.map { tag ->
            return@map TagData(tag.key.location, tag.map { ForgeRegistries.ITEMS.getKey(it).toString() })
        } ?: emptyList()

        transaction {
            DatabaseUtil.reset(ItemTagsTable)

            ItemTagsTable.batchInsert(itemTags) {
                this[ItemTagsTable.namespace] = it.id.namespace
                this[ItemTagsTable.path] = it.id.path
                this[ItemTagsTable.entry] = it.entry
            }
        }
    }

    private data class TagData(
        val id: ResourceLocation,
        val entry: List<String>
    )
}