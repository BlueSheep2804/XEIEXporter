package dev.bluesheep.xeiexporter.exporter.resources

import mezz.jei.api.ingredients.IIngredientHelper
import mezz.jei.api.ingredients.IIngredientRenderer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

class JEIIngredientRenderer(
    override val name: String,
    val ingredients: List<Any>,
    val ingredientHelper: IIngredientHelper<Any>,
    val ingredientRenderer: IIngredientRenderer<Any>
) : AbstractRenderSystemRenderer() {
    override val renderList: Map<ResourceLocation, (GuiGraphics) -> Unit>
        get() {
            return ingredients.associate { ingredient ->
                return@associate ingredientHelper.getResourceLocation(ingredient) to { guiGraphics ->
                    ingredientRenderer.render(guiGraphics, ingredient, 0, 0)
                }
            }
        }
}
