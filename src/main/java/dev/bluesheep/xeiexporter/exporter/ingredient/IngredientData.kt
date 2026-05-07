package dev.bluesheep.xeiexporter.exporter.ingredient

import net.minecraft.resources.ResourceLocation

data class IngredientData(
    val type: String,
    val ingredientId: ResourceLocation,
    val uniqueId: String,
    val descriptionId: String
) {
    val namespace: String
        get() = ingredientId.namespace
    val path: String
        get() = ingredientId.path
}
