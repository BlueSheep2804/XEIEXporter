package dev.bluesheep.xeiexporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExportUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveExportFile(Object src, Path path) {
        try {
            Files.writeString(path, gson.toJson(src));
        } catch (IOException e) {
            XEIExporter.LOGGER.warn("export failed");
        }
    }

    public static void mkdir(Path path) {
        mkdir(path.toFile());
    }

    public static void mkdir(File path) {
        if (!path.exists()) {
            if (!path.mkdir()) {
                XEIExporter.LOGGER.warn("mkdir failed");
            }
        }
    }

    public static Path resourceLocationToPath(Path base, ResourceLocation recipeId, String extension) {
        var path = base.resolve(recipeId.getNamespace());
        String[] pathArray = (recipeId.getPath() + extension).split("/");
        if (pathArray.length == 1) {
            path = path.resolve(pathArray[0]);
        } else {
            for (int i = 0; i < pathArray.length - 1; i++) {
                path = path.resolve(pathArray[i]);
            }
        }
        path.getParent().toFile().mkdirs();
        return path;
    }

    public static Path resourceLocationToJson(Path base, ResourceLocation resourceLocation) {
        return resourceLocationToPath(base, resourceLocation, ".json");
    }
}
