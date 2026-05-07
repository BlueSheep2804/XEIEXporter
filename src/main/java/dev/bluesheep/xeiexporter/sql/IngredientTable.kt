package dev.bluesheep.xeiexporter.sql

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object IngredientTable : IntIdTable("ingredient") {
    val type = text("type")
    val uniqueId = text("uniqueId")
    val namespace = text("namespace")
    val path = text("path")
    val descriptionId = text("descriptionId")
}
