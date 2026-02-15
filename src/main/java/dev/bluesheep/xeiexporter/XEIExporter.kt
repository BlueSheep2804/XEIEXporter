package dev.bluesheep.xeiexporter

import com.mojang.brigadier.context.CommandContext
import com.mojang.logging.LogUtils
import dev.bluesheep.xeiexporter.debug.DebugRegister
import dev.bluesheep.xeiexporter.recipe.RecipeExporter
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.locale.Language
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.loading.FMLPaths
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import java.nio.file.Path

@Mod(value = XEIExporter.MODID)
object XEIExporter {
    const val MODID: String = "xeiexporter"
    @JvmField
    val LOGGER: Logger = LogUtils.getLogger()

    @JvmField
    val EXPORT_DIR: Path = FMLPaths.GAMEDIR.get().resolve("export")
    @JvmField
    val EXPORT_ASSETS_DIR: Path = EXPORT_DIR.resolve("assets")
    private val EXPORT_LANG_FILE: Path = EXPORT_DIR.resolve("lang.json")

    init {
        val modEventBus = MOD_CONTEXT.getKEventBus()

        DebugRegister.BLOCKS.register(modEventBus)
        DebugRegister.ITEMS.register(modEventBus)
        DebugRegister.CREATIVE_MODE_TABS.register(modEventBus)
    }

    @EventBusSubscriber(modid = MODID, value = [Dist.CLIENT])
    object ClientModEvents {
        private val recipeExporter = RecipeExporter()

        @SubscribeEvent
        fun registerClientCommand(event: RegisterClientCommandsEvent) {
            event.dispatcher.register(
                Commands.literal("export").executes { context: CommandContext<CommandSourceStack> ->
                    ExportUtil.mkdir(EXPORT_DIR)
                    ExportUtil.mkdir(EXPORT_ASSETS_DIR)

                    exportLanguages()
                    ItemExporter.exportItems()
                    recipeExporter.exportRecipes()
                    return@executes 0
                }
            )
        }

        private fun exportLanguages() {
            ExportUtil.saveExportFile(Language.getInstance().languageData, EXPORT_LANG_FILE)
        }
    }
}
