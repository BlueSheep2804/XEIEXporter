package dev.bluesheep.xeiexporter.exporter.resources

import dev.bluesheep.xeiexporter.JEIExporterPlugin
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import kotlin.to

class RecipeCategoryRenderer : AbstractRenderSystemRenderer() {
    override val name: String
        get() = "recipe_type"

    override val renderList: Map<ResourceLocation, (GuiGraphics) -> Unit>
        get() {
            val runtime = JEIExporterPlugin.runtime ?: return emptyMap()
            return runtime.jeiHelpers.allRecipeTypes.toList().associate { recipeType ->
                return@associate recipeType.uid to { guiGraphics ->
                    runtime.recipeManager.getRecipeCategory(recipeType).icon?.draw(guiGraphics)
                }
            }
        }
}