package dev.bluesheep.xeiexporter.api.event

import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import mezz.jei.api.ingredients.IIngredientType

class RegisterIngredientExtraHelperEvent(
    private val extraHelpers: MutableMap<Class<*>, IIngredientExtraHelper<*>>
) : AbstractXEIExporterEvent() {
    fun register(clazz: Class<*>, extraHelper: IIngredientExtraHelper<*>) {
        extraHelpers.put(clazz, extraHelper)
    }

    fun register(ingredientType: IIngredientType<*>, extraHelper: IIngredientExtraHelper<*>) {
        register(ingredientType.ingredientClass, extraHelper)
    }
}