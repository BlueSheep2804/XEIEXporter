package dev.bluesheep.xeiexporter.sql

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object RecipesTable : IntIdTable("recipes") {
    val namespace = text("namespace")
    val path = text("path")
    val type = text("type")
    val input = array<String>("input")
    val output = array<String>("output")
}