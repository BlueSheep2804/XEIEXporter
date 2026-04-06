package dev.bluesheep.xeiexporter

import com.mojang.logging.LogUtils
import dev.bluesheep.xeiexporter.debug.DebugRegister
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import net.minecraft.commands.Commands
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.loading.FMLLoader
import net.minecraftforge.fml.loading.FMLPaths
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import java.nio.file.Path

@Mod(value = XEIExporter.MODID)
object XEIExporter {
    const val MODID: String = "xeiexporter"
    @JvmField
    val LOGGER: Logger = LogUtils.getLogger()

    @JvmField
    val EXPORT_DIR: Path = FMLPaths.GAMEDIR.get().resolve(MODID)
    @JvmField
    val EXPORT_ASSETS_DIR: Path = EXPORT_DIR.resolve("assets")

    init {
        val modEventBus = MOD_CONTEXT.getKEventBus()

        FORGE_BUS.register(this)

        if (!FMLLoader.isProduction()) {
            DebugRegister.BLOCKS.register(modEventBus)
            DebugRegister.ITEMS.register(modEventBus)
            DebugRegister.CREATIVE_MODE_TABS.register(modEventBus)
        }

        LOADING_CONTEXT.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    @SubscribeEvent
    fun register(event: RegisterCommandsEvent) {
        event.dispatcher.register(
            Commands.literal("export").executes { context ->
                context.source.player?.let { ExportUtil.exportData(it) }
                return@executes 0
            }
        )
    }

    @EventBusSubscriber(modid = MODID, value = [Dist.CLIENT])
    object ClientModEvents {
        @SubscribeEvent
        fun registerClientCommand(event: RegisterClientCommandsEvent) {
            event.dispatcher.register(
                Commands.literal("exportc").executes { context ->
                    ExportUtil.exportAsset()
                    return@executes 0
                }
            )
        }
    }
}
