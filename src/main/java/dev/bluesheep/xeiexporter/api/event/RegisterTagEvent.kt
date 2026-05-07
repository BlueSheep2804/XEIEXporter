package dev.bluesheep.xeiexporter.api.event

import mezz.jei.api.ingredients.IIngredientType
import net.minecraft.resources.ResourceLocation

class RegisterTagEvent(
    private val tags: MutableMap<IIngredientType<*>, () -> Map<ResourceLocation, List<ResourceLocation>>>
) : AbstractXEIExporterEvent() {
    fun register(ingredientType: IIngredientType<*>, provider: () -> Map<ResourceLocation, List<ResourceLocation>>) {
        tags.put(ingredientType, provider)
    }
}