package dev.bluesheep.xeiexporter.exporter.resources

import com.mojang.blaze3d.platform.NativeImage
import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.exporter.ExportUtil.resourceLocationToPath
import net.minecraft.resources.ResourceLocation
import java.io.IOException

class RenderExporter {
    private val renderers = mutableListOf<IRenderer>()

    init {
        renderers.add(ItemRenderer())
    }

    fun export() {
        renderers.forEach { renderer ->
            renderer.entries.forEach { (location, image) ->
                exportImage(renderer.name, location, image)
            }
        }
    }

    private fun exportImage(parent: String, itemId: ResourceLocation, nativeImage: NativeImage) {
        try {
            nativeImage.writeToFile(
                resourceLocationToPath(
                    XEIExporter.EXPORT_ASSETS_DIR.resolve(parent),
                    itemId,
                    ".png"
                )
            )
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            nativeImage.close()
        }
    }
}