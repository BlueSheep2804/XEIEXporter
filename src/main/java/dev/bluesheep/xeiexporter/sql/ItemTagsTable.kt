package dev.bluesheep.xeiexporter.sql

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ItemTagsTable : IntIdTable("tags_item") {
    val namespace = text("namespace")
    val path = text("path")
    val entry = array<String>("entry")
}