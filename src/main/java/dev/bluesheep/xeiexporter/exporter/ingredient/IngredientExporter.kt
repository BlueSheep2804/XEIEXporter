package dev.bluesheep.xeiexporter.exporter.ingredient

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.IIngredientExtraHelper
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.IngredientTable
import dev.bluesheep.xeiexporter.sql.IngredientTypeTable
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
            DatabaseUtil.reset(IngredientTypeTable)

            val ingredientTypes = ingredientManager?.registeredIngredientTypes?.map { ingredientType ->
                val extraHelper = extraHelpers[ingredientType.ingredientClass] as IIngredientExtraHelper<Any>?
                return@map IngredientTypeData(
                    ingredientType.uid,
                    extraHelper?.ingredientNameKey ?: "",
                    extraHelper?.isMilliUnit ?: false
                )
            } ?: emptyList()
            IngredientTypeTable.batchInsert(ingredientTypes) {
                this[IngredientTypeTable.id] = it.id
                this[IngredientTypeTable.translationKey] = it.translationKey
                this[IngredientTypeTable.isMilliUnit] = it.isMilliUnit
            }

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
                            if (helper.hasSubtypes(ingredient)) helper.getUniqueId(ingredient, UidContext.Ingredient) else "",
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

    private data class IngredientTypeData(
        val id: String,
        val translationKey: String,
        val isMilliUnit: Boolean
    )
}