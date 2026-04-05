package dev.bluesheep.xeiexporter.exporter.resources

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import dev.bluesheep.xeiexporter.XEIExporter
import mezz.jei.common.platform.IPlatformFluidHelperInternal
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries

class FluidRenderer : AbstractRenderSystemRenderer() {
    override val name: String = "fluids"

    override val renderList: Map<ResourceLocation, (GuiGraphics) -> Unit>
        get() {
            val fluidHelper = JEIExporterPlugin.runtime?.jeiHelpers?.platformFluidHelper ?: return emptyMap()
            return ForgeRegistries.FLUIDS.entries.associate { (key, fluid) ->
                key.location() to { guiGraphics ->
                    try {
                        (fluidHelper as IPlatformFluidHelperInternal<FluidStack>).createRenderer(1000, false, 64, 64)
                            .render(guiGraphics, FluidStack(fluid, 1000), 0, 0)
                    } catch (_: ClassCastException) {
                        XEIExporter.LOGGER.error("ClassCastException by ${key.location()} on FluidRenderer")
                    }
                }
            }
        }
}