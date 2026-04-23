package dev.bluesheep.xeiexporter.sql

import dev.bluesheep.xeiexporter.api.recipe.RecipeStackData
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.json.jsonb

object RecipesTable : IntIdTable("recipes") {
    val namespace = text("namespace")
    val path = text("path")
    val type = text("type")
    val input = jsonb<List<List<RecipeStackData>>>("input", Json.Default, ListSerializer(ListSerializer(RecipeStackData.serializer())))
    val output = jsonb<List<List<RecipeStackData>>>("output", Json.Default)
}