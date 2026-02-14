package dev.bluesheep.xeiexporter;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class ExporterJeiPlugin implements IModPlugin {
    public static IJeiRuntime runtime = null;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(XEIExporter.MODID, "jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }
}
