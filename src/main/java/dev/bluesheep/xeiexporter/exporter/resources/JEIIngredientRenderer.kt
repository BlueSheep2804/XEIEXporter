package dev.bluesheep.xeiexporter.exporter.resources

import dev.bluesheep.xeiexporter.api.renderer.AbstractRenderSystemRenderer
import mezz.jei.api.ingredients.IIngredientHelper
import mezz.jei.api.ingredients.IIngredientRenderer
import mezz.jei.api.ingredients.subtypes.UidContext
import net.minecraft.client.gui.GuiGraphics

class JEIIngredientRenderer(
    override val name: String,
    val ingredients: List<Any>,
    val ingredientHelper: IIngredientHelper<Any>,
    val ingredientRenderer: IIngredientRenderer<Any>
) : AbstractRenderSystemRenderer() {
    override val renderList: Map<String, (GuiGraphics) -> Unit>
        get() {
            return ingredients.associate { ingredient ->
                val render = { guiGraphics: GuiGraphics ->
                    ingredientRenderer.render(guiGraphics, ingredient, 0, 0)
                }

                if (ingredientHelper.hasSubtypes(ingredient)) {
                    val path = escape(ingredientHelper.getUniqueId(ingredient, UidContext.Ingredient))
                    if (path != null) {
                        return@associate path to render
                    }
                }
                val id = ingredientHelper.getResourceLocation(ingredient)
                return@associate "${id.namespace}/${id.path}" to render
            }
        }

    private fun escape(id: String): String? {
        val split = id.split(':', limit = 3)
        if (split.size < 2) return null
        return split.joinToString("/")
            .replace(':', ';')
            .replace(Regex("[\"?#*<>]"), "-")
    }
}
