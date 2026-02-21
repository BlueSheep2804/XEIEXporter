package dev.bluesheep.xeiexporter

import com.mojang.brigadier.context.CommandContext
import com.mojang.logging.LogUtils
import dev.bluesheep.xeiexporter.debug.DebugRegister
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.loading.FMLPaths
import org.jetbrains.exposed.v1.jdbc.Database
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.Logger
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

        DebugRegister.BLOCKS.register(modEventBus)
        DebugRegister.ITEMS.register(modEventBus)
        DebugRegister.CREATIVE_MODE_TABS.register(modEventBus)

        LOADING_CONTEXT.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    @EventBusSubscriber(modid = MODID, value = [Dist.CLIENT])
    object ClientModEvents {
        @SubscribeEvent
        fun registerClientCommand(event: RegisterClientCommandsEvent) {
            event.dispatcher.register(
                Commands.literal("export").executes { context: CommandContext<CommandSourceStack> ->
                    return@executes ExportUtil.export()
                }
            )
        }
    }
}
