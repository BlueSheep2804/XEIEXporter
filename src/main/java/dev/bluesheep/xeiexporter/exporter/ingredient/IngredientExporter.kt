package dev.bluesheep.xeiexporter.exporter.ingredient

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.api.IIngredientModifier
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import dev.bluesheep.xeiexporter.exporter.ingredient.DefaultModifier
import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.IngredientTable
import mezz.jei.api.ingredients.IIngredientType
import mezz.jei.api.ingredients.subtypes.UidContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object IngredientExporter {
    val ingredientModifiers = mutableMapOf<Class<*>, IIngredientModifier<*>>()
    private val defaultModifier = DefaultModifier()

    fun export() {
        val ingredientManager = JEIExporterPlugin.runtime?.ingredientManager

        transaction {
            DatabaseUtil.reset(IngredientTable)

            ingredientManager?.registeredIngredientTypes?.forEach { ingredientType ->
                val helper = ingredientManager.getIngredientHelper(ingredientType as IIngredientType<Any>)

                val modifier = ingredientModifiers[ingredientType.ingredientClass] as IIngredientModifier<Any>?

                val allIngredients = listOf(
                    *(modifier?.allIngredients ?: ingredientManager.getAllIngredients(ingredientType)).toTypedArray(),
                    *(modifier ?: defaultModifier).additionalIngredients.toTypedArray()
                )
                if (allIngredients.isEmpty()) {
                    ExportUtil.dataLogWarning("ingredient.none", ExportUtil.hoverIngredientTypeUid(ingredientType.uid))
                } else {
                    val ingredients = allIngredients.map { ingredient ->
                        IngredientData(
                            ingredientType.uid,
                            helper.getResourceLocation(ingredient),
                            helper.getUniqueId(ingredient, UidContext.Ingredient),
                            modifier?.mapDescriptionId(ingredient) ?: defaultModifier.mapDescriptionId(ingredient)
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