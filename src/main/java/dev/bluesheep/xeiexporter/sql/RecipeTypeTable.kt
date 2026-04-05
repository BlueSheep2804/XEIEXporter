package dev.bluesheep.xeiexporter.sql

import org.jetbrains.exposed.v1.core.Table

object RecipeTypeTable : Table("recipe_type") {
    val id = text("id")
    val catalyst = array<String>("catalyst")
    val inputSize = integer("inputSize")
    val outputSize = integer("outputSize")
    val titleKey = text("titleKey")
    val titleFallback = text("titleFallback")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}