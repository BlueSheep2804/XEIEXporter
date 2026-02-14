package dev.bluesheep.xeiexporter;

import dev.bluesheep.xeiexporter.debug.DebugRegister;
import dev.bluesheep.xeiexporter.recipe.RecipeExporter;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.Commands;
import net.minecraft.locale.Language;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.file.Path;

@Mod(value = XEIExporter.MODID)
public class XEIExporter
{
    public static final String MODID = "xeiexporter";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Path EXPORT_DIR = FMLPaths.GAMEDIR.get().resolve("export");
    public static final Path EXPORT_ASSETS_DIR = EXPORT_DIR.resolve("assets");
    private static final Path EXPORT_LANG_FILE = EXPORT_DIR.resolve("lang.json");

    public XEIExporter()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DebugRegister.BLOCKS.register(modEventBus);
        DebugRegister.ITEMS.register(modEventBus);
        DebugRegister.CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        private static final RecipeExporter recipeExporter = new RecipeExporter();

        @SubscribeEvent
        public static void registerClientCommand(RegisterClientCommandsEvent event) {
            event.getDispatcher().register(
                    Commands.literal("export").executes(context -> {
                        ExportUtil.mkdir(EXPORT_DIR);
                        ExportUtil.mkdir(EXPORT_ASSETS_DIR);

                        exportLanguages();
                        ItemExporter.exportItems();
                        recipeExporter.exportRecipes();
                        return 0;
                    })
            );
        }

        private static void exportLanguages() {
            ExportUtil.saveExportFile(Language.getInstance().getLanguageData(), EXPORT_LANG_FILE);
        }
    }
}
