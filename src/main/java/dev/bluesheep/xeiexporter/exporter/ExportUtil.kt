package dev.bluesheep.xeiexporter.exporter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.XEIExporter.EXPORT_ASSETS_DIR
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeExporter
import dev.bluesheep.xeiexporter.exporter.resources.ItemRendererExporter
import dev.bluesheep.xeiexporter.exporter.resources.LanguageExporter
import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object ExportUtil {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val recipeExporter = RecipeExporter()
    private val tagExporter = TagExporter()

    fun export(): Int {
        DatabaseUtil.connect()

        ItemExporter.exportItems()
        tagExporter.export()
        recipeExporter.exportRecipes()

        return 0
    }

    fun exportClient(): Int {
        EXPORT_ASSETS_DIR.toFile().mkdirs()

        LanguageExporter.export()
        ItemRendererExporter.export()

        return 0
    }

    @JvmStatic
    fun saveExportFile(src: Any?, path: Path) {
        try {
            Files.writeString(path, gson.toJson(src))
        } catch (_: IOException) {
            XEIExporter.LOGGER.warn("export failed")
        }
    }

    @JvmStatic
    fun mkdir(path: Path) {
        mkdir(path.toFile())
    }

    @JvmStatic
    fun mkdir(path: File) {
        if (!path.exists()) {
            if (!path.mkdir()) {
                XEIExporter.LOGGER.warn("mkdir failed")
            }
        }
    }

    @JvmStatic
    fun resourceLocationToPath(base: Path, resourceLocation: ResourceLocation, extension: String): Path {
        var path = base.resolve(resourceLocation.namespace)
        val pathArray = "${resourceLocation.path}$extension".split("/")
        if (pathArray.size == 1) {
            path = path.resolve(pathArray[0])
        } else {
            for (i in 0..<pathArray.size - 1) {
                path = path.resolve(pathArray[i])
            }
        }
        path.parent.toFile().mkdirs()
        return path
    }

    @JvmStatic
    fun resourceLocationToJson(base: Path, resourceLocation: ResourceLocation): Path {
        return resourceLocationToPath(base, resourceLocation, ".json")
    }

    @JvmStatic
    fun rl(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(XEIExporter.MODID, path)
    }

    @JvmStatic
    fun rlVanilla(path: String): ResourceLocation {
        return ResourceLocation.withDefaultNamespace(path)
    }
}