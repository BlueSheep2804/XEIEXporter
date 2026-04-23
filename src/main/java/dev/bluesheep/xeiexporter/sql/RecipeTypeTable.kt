package dev.bluesheep.xeiexporter.sql

import dev.bluesheep.xeiexporter.api.recipe.RecipeStackData
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.jsonb

object RecipeTypeTable : Table("recipe_type") {
    val id = text("id")
    val catalyst = jsonb<List<RecipeStackData>>("catalyst", Json.Default)
    val inputSize = integer("inputSize")
    val outputSize = integer("outputSize")
    val titleKey = text("titleKey")
    val titleFallback = text("titleFallback")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}