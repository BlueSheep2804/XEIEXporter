package dev.bluesheep.xeiexporter.sql

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object FluidsTable : IntIdTable("fluids") {
    val namespace = text("namespace")
    val name = text("name")
    val descriptionId = text("descriptionId")
    val temperature = integer("temperature")
}