package dev.bluesheep.xeiexporter.api.event

import mezz.jei.api.ingredients.IIngredientType
import net.minecraftforge.registries.IForgeRegistry

class RegisterTagEvent(
    private val tags: MutableMap<IIngredientType<*>, IForgeRegistry<*>>
) : AbstractXEIExporterEvent() {
    fun register(ingredientType: IIngredientType<*>, registry: IForgeRegistry<*>) {
        tags.put(ingredientType, registry)
    }
}