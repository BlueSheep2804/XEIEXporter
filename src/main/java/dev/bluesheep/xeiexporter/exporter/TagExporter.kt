package dev.bluesheep.xeiexporter.exporter

import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.TagTable
import mezz.jei.api.ingredients.IIngredientType
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.IForgeRegistry
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object TagExporter {
    val tagRegistries = mutableMapOf<IIngredientType<*>, IForgeRegistry<*>>()

    fun export() {
        transaction {
            DatabaseUtil.reset(TagTable)

            tagRegistries.forEach { (type, registry) ->
                registry as IForgeRegistry<Any>
                val tags = registry.tags()?.associate {
                    it.key.location to it.map {
                        entry -> registry.getKey(entry) ?: ExportUtil.UNKNOWN
                    }.map(ResourceLocation::toString)
                } ?: emptyMap()

                TagTable.batchInsert(tags.entries) {
                    this[TagTable.type] = type.uid
                    this[TagTable.namespace] = it.key.namespace
                    this[TagTable.path] = it.key.path
                    this[TagTable.entry] = it.value
                }

                ExportUtil.dataLogComplete(
                    "tag",
                    tags.size,
                    ExportUtil.hoverIngredientTypeUid(type.uid)
                )
            }
        }
    }
}