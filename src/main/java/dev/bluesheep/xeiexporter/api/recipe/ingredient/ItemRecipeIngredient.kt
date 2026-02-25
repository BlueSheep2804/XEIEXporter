package dev.bluesheep.xeiexporter.api.recipe.ingredient

import dev.bluesheep.xeiexporter.mixin.IngredientAccessor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.registries.ForgeRegistries

class ItemRecipeIngredient(val ingredient: Ingredient) : AbstractRecipeIngredient<ItemStack>(ingredient.items.asList()) {
    constructor(ingredient: ItemStack) : this(listOf(ingredient))

    constructor(ingredients: List<ItemStack>) : this(Ingredient.of(*ingredients.toTypedArray()))

    override fun export(): List<String> {
        val values = (ingredient as IngredientAccessor).values.asList()
        return values.map {
            if (it is Ingredient.TagValue) {
                "#" + it.serialize().get("tag").asString
            } else {
                it.items.joinToString { itemStack ->
                    val count = if (itemStack.count > 1) "${itemStack.count}x " else ""
                    "${count}${ForgeRegistries.ITEMS.getKey(itemStack.item)}"
                }
            }
        }
    }
}