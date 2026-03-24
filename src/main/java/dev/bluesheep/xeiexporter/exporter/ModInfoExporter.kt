package dev.bluesheep.xeiexporter.exporter

import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.ModsTable
import net.minecraftforge.fml.loading.FMLLoader
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

object ModInfoExporter {
    fun export() {
        val mods = FMLLoader.getLoadingModList().mods.map {
            ModData(
                it.modId,
                it.displayName,
                it.version.toString(),
                it.owningFile.file.fileName,
                it.config.getConfigElement<String>("authors").getOrDefault(""),
                it.description,
                it.modURL.getOrNull()?.toString() ?: "",
                it.owningFile.license
            )
        }

        transaction {
            DatabaseUtil.reset(ModsTable)

            ModsTable.batchInsert(mods) {
                this[ModsTable.modId] = it.modId
                this[ModsTable.displayName] = it.displayName
                this[ModsTable.version] = it.version
                this[ModsTable.fileName] = it.fileName
                this[ModsTable.authors] = it.authors
                this[ModsTable.description] = it.description
                this[ModsTable.url] = it.url
                this[ModsTable.license] = it.license
            }
        }
    }

    private data class ModData(
        val modId: String,
        val displayName: String,
        val version: String,
        val fileName: String,
        val authors: String,
        val description: String,
        val url: String,
        val license: String
    )
}