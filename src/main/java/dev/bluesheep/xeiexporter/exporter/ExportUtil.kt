package dev.bluesheep.xeiexporter.exporter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.XEIExporter.EXPORT_ASSETS_DIR
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeExporter
import dev.bluesheep.xeiexporter.exporter.resources.LanguageExporter
import dev.bluesheep.xeiexporter.exporter.resources.RenderExporter
import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.common.util.Lazy
import org.postgresql.util.PSQLException
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.reflect.jvm.jvmName

object ExportUtil {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val recipeExporter = RecipeExporter()
    private val tagExporter = TagExporter()
    private val renderExporter = Lazy.of { RenderExporter() }

    private var serverPlayer: ServerPlayer? = null

    fun exportData(player: ServerPlayer) {
        serverPlayer = player
        CompletableFuture.runAsync {
            DatabaseUtil.connect()

            try {
                dataLogStart("mod")
                val modCount = ModInfoExporter.export()
                dataLogComplete("mod", modCount)

                dataLogStart("registry")
                ItemExporter.exportItems()
                FluidExporter.export()
                dataLogComplete("registry", 0, "")  // TODO: レジストリをアドオン式にしたときに修正

                dataLogStart("tag")
                tagExporter.export()
                dataLogComplete("tag", 0, "")  // TODO: タグをアドオン式にしたときに修正

                dataLogStart("recipe")
                val recipeCount = recipeExporter.exportRecipes()
                dataLogComplete("recipe", recipeCount)
            } catch (e: Exception) {
                when (e) {
                    is PSQLException -> {
                        sendSystemMessage(
                            Component.translatable("xeiexporter.error.database", e::class.jvmName)
                                .withStyle(
                                    Style.EMPTY
                                        .withColor(ChatFormatting.RED)
                                        .withHoverEvent(
                                            HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(e.message ?: ""))
                                        )
                                )
                        )
                    }
                    else -> {
                        sendSystemMessage(
                            Component.translatable("xeiexporter.error", e::class.jvmName)
                                .withStyle(
                                    Style.EMPTY
                                        .withColor(ChatFormatting.RED)
                                        .withHoverEvent(
                                            HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(e.message ?: ""))
                                        )
                                )
                        )
                    }
                }
            }

            sendSystemMessage(Component.translatable("xeiexporter.complete").withStyle(ChatFormatting.GREEN))
            serverPlayer = null
        }
    }

    fun sendSystemMessage(component: Component) {
        serverPlayer?.sendSystemMessage(component)
    }

    fun dataLogStart(key: String) = logStart(::sendSystemMessage, key)
    fun dataLogComplete(key: String, vararg args: Any) = logComplete(::sendSystemMessage, key, *args)

    fun exportAsset() {
        EXPORT_ASSETS_DIR.toFile().mkdirs()

        assetLogStart("render")
        renderExporter.get().export()

        CompletableFuture.runAsync {
            assetLogStart("language")
            val langCount = LanguageExporter.export()
            assetLogComplete("language", langCount)

            sendSystemMessageToClient(Component.translatable("xeiexporter.complete").withStyle(ChatFormatting.GREEN))
        }
    }

    fun sendSystemMessageToClient(component: Component) {
        Minecraft.getInstance().player?.sendSystemMessage(component)
    }

    fun assetLogStart(key: String) = logStart(::sendSystemMessageToClient, key)
    fun assetLogComplete(key: String, vararg args: Any) = logComplete(::sendSystemMessageToClient, key, *args)

    @JvmStatic
    fun saveExportFile(src: Any?, path: Path) {
        try {
            Files.writeString(path, gson.toJson(src))
        } catch (error: IOException) {
            XEIExporter.LOGGER.warn("export failed: ${error.toString()}")
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
            for (i in 0..<pathArray.size) {
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

    @JvmStatic
    fun rlJei(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("jei", path)
    }

    private fun logStart(send: (Component) -> Unit, key: String) {
        send(Component.translatable("xeiexporter.${key}.start"))
    }

    private fun logComplete(send: (Component) -> Unit, key: String, vararg args: Any) {
        send(Component.translatable("xeiexporter.${key}.complete", *args).withStyle(ChatFormatting.GREEN))
    }
}