package dev.bluesheep.xeiexporter.exporter.recipe

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraft.world.level.Level
import net.minecraftforge.registries.ForgeRegistries

class ShapelessRecipeExporter internal constructor() {
    fun export(recipe: ShapelessRecipe, level: Level): JsonObject {
        val json = JsonObject()
        json.addProperty("type", "minecraft:crafting_shapeless")
        json.addProperty("category", recipe.category().serializedName)
        if (!recipe.getGroup().isEmpty()) {
            json.addProperty("group", recipe.getGroup())
        }

        val ingredients = JsonArray()
        recipe.ingredients.forEach { ingredients.add(it.toJson()) }
        json.add("ingredients", ingredients)

        val itemStack = recipe.getResultItem(level.registryAccess())
        val result = JsonObject()
        result.addProperty(
            "item",
            ForgeRegistries.ITEMS.getKey(itemStack.item).toString()
        )
        result.addProperty("count", itemStack.count)
        CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, itemStack.tag)
            .result()
            .ifPresent { result.add("tag", it) }
        json.add("result", result)

        return json
    }
}
