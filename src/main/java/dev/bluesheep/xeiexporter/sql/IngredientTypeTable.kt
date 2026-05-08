package dev.bluesheep.xeiexporter.sql

import org.jetbrains.exposed.v1.core.Table

object IngredientTypeTable : Table("ingredient_type") {
    val id = text("id")
    val translationKey = text("translation_key")
    val isMilliUnit = bool("isMilliUnit")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}