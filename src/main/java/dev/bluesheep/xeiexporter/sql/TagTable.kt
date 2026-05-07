package dev.bluesheep.xeiexporter.sql

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object TagTable : IntIdTable("tag") {
    val type = text("type")
    val namespace = text("namespace")
    val path = text("path")
    val entry = array<String>("entry")
}