package dev.bluesheep.xeiexporter.exporter.ingredient

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper

class DefaultExtraHelper : IIngredientExtraHelper<Any> {
    override fun getDescriptionId(ingredient: Any): String {
        val helper = JEIExporterPlugin.Companion.runtime?.ingredientManager?.getIngredientHelper(ingredient) ?: return "xeiexporter.unknown"
        return helper.getResourceLocation(ingredient).toLanguageKey()
    }

    override val allIngredients: Collection<Any>
        get() = emptyList()
}