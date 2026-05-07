package dev.bluesheep.xeiexporter.exporter

import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.TagTable
import mezz.jei.api.ingredients.IIngredientType
import net.minecraft.resources.ResourceLocation
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object TagExporter {
    val tagRegistries = mutableMapOf<IIngredientType<*>, () -> Map<ResourceLocation, List<ResourceLocation>>>()

    fun export() {
        transaction {
            DatabaseUtil.reset(TagTable)

            tagRegistries.forEach { (type, value) ->
                val tags = value.invoke().entries
                TagTable.batchInsert(tags) {
                    this[TagTable.type] = type.uid
                    this[TagTable.namespace] = it.key.namespace
                    this[TagTable.path] = it.key.path
                    this[TagTable.entry] = it.value.map(ResourceLocation::toString)
                }
                ExportUtil.dataLogComplete("tag", tags.size, ExportUtil.hoverIngredientTypeUid(type.uid))
            }
        }
    }
}