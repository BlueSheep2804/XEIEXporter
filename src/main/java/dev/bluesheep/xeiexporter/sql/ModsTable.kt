package dev.bluesheep.xeiexporter.sql

import org.jetbrains.exposed.v1.core.Table

object ModsTable : Table("mods") {
    val modId = text("modId")
    val displayName = text("displayName")
    val version = text("version")
    val fileName = text("fileName")
    val authors = text("authors")
    val description = text("description")
    val url = text("url")
    val license = text("license")

    override val primaryKey = PrimaryKey(modId)
}