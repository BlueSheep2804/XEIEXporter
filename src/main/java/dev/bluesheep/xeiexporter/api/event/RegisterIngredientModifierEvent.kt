package dev.bluesheep.xeiexporter.api.event

import dev.bluesheep.xeiexporter.api.IIngredientModifier
import mezz.jei.api.ingredients.IIngredientType

class RegisterIngredientModifierEvent(
    private val modifiers: MutableMap<Class<*>, IIngredientModifier<*>>
) : AbstractXEIExporterEvent() {
    fun register(clazz: Class<*>, modifier: IIngredientModifier<*>) {
        modifiers.put(clazz, modifier)
    }

    fun register(ingredientType: IIngredientType<*>, modifier: IIngredientModifier<*>) {
        register(ingredientType.ingredientClass, modifier)
    }
}