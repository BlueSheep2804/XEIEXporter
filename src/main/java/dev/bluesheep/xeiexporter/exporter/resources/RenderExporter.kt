package dev.bluesheep.xeiexporter.exporter.resources

import com.mojang.blaze3d.platform.NativeImage
import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.api.renderer.IRenderer
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import mezz.jei.api.ingredients.IIngredientType
import java.io.IOException

class RenderExporter {
    private val renderers = mutableListOf<IRenderer>()

    init {
        renderers.add(RecipeCategoryRenderer())

        val ingredientManager = JEIExporterPlugin.runtime?.ingredientManager
        ingredientManager?.registeredIngredientTypes?.forEach { ingredientType ->
            renderers.add(
                JEIIngredientRenderer(
                    ingredientType.uid,
                    ingredientManager.getAllIngredients(ingredientType).toList(),
                    ingredientManager.getIngredientHelper(ingredientType as IIngredientType<Any>),
                    ingredientManager.getIngredientRenderer(ingredientType)
                )
            )
        }
    }

    fun export() {
        renderers.forEach { renderer ->
            var count = 0
            renderer.entries.forEach { (location, image) ->
                exportImage(renderer.name, location, image)
                count++
            }
            ExportUtil.assetLogComplete(
                "render",
                count,
                renderer.name
            )
        }
    }

    private fun exportImage(parent: String, entryId: String, nativeImage: NativeImage) {
        try {
            val path = XEIExporter.EXPORT_ASSETS_DIR
                .resolve(parent)
                .resolve("${entryId}.png")
            path.parent.toFile().mkdirs()
            nativeImage.writeToFile(
                path
            )
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            nativeImage.close()
        }
    }
}