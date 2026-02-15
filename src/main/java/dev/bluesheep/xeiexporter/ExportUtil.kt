package dev.bluesheep.xeiexporter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object ExportUtil {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

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
}
