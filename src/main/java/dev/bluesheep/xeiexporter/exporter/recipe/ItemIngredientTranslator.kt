package dev.bluesheep.xeiexporter.exporter.recipe

import dev.bluesheep.xeiexporter.api.recipe.ingredient.AbstractRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import net.minecraft.world.item.ItemStack

class ItemIngredientTranslator : IIngredientTranslator<ItemStack> {
    override fun export(ingredient: Any): AbstractRecipeIngredient<ItemStack>? {
        if (ItemStack::class.isInstance(ingredient)) {
            return ItemRecipeIngredient(ingredient as ItemStack)
        }
        return null
    }
}