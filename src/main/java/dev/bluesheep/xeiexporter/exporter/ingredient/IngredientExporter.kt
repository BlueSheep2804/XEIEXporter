package dev.bluesheep.xeiexporter.exporter.ingredient

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.IngredientTable
import mezz.jei.api.ingredients.IIngredientType
import mezz.jei.api.ingredients.subtypes.UidContext
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object IngredientExporter {
    val extraHelpers = mutableMapOf<Class<*>, IIngredientExtraHelper<*>>()
    private val defaultExtraHelper = DefaultExtraHelper()

    fun export() {
        val ingredientManager = JEIExporterPlugin.runtime?.ingredientManager

        transaction {
            DatabaseUtil.reset(IngredientTable)

            ingredientManager?.registeredIngredientTypes?.forEach { ingredientType ->
                val helper = ingredientManager.getIngredientHelper(ingredientType as IIngredientType<Any>)

                val extraHelper = extraHelpers[ingredientType.ingredientClass] as IIngredientExtraHelper<Any>?

                val allIngredients = listOf(
                    *(extraHelper?.allIngredients ?: ingredientManager.getAllIngredients(ingredientType)).toTypedArray(),
                    *(extraHelper ?: defaultExtraHelper).additionalIngredients.toTypedArray()
                )
                if (allIngredients.isEmpty()) {
                    ExportUtil.dataLogWarning("ingredient.none", ExportUtil.hoverIngredientTypeUid(ingredientType.uid))
                } else {
                    val ingredients = allIngredients.map { ingredient ->
                        IngredientData(
                            ingredientType.uid,
                            helper.getResourceLocation(ingredient),
                            helper.getUniqueId(ingredient, UidContext.Ingredient),
                            extraHelper?.getDescriptionId(ingredient) ?: defaultExtraHelper.getDescriptionId(ingredient)
                        )
                    }

                    IngredientTable.batchInsert(ingredients) { ingredient ->
                        this[IngredientTable.type] = ingredient.type
                        this[IngredientTable.namespace] = ingredient.namespace
                        this[IngredientTable.path] = ingredient.path
                        this[IngredientTable.uniqueId] = ingredient.uniqueId
                        this[IngredientTable.descriptionId] = ingredient.descriptionId
                    }
                    ExportUtil.dataLogComplete("ingredient", ingredients.size, ExportUtil.hoverIngredientTypeUid(ingredientType.uid))
                }
            }
        }
    }
}