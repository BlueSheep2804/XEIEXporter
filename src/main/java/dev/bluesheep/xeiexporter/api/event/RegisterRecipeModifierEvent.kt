package dev.bluesheep.xeiexporter.api.event

import dev.bluesheep.xeiexporter.api.recipe.IRecipeModifier
import mezz.jei.api.recipe.RecipeType

class RegisterRecipeModifierEvent(
    private val registered: MutableMap<RecipeType<Any>, MutableList<IRecipeModifier<Any>>>
) : AbstractXEIExporterEvent() {
    fun <T> register(recipeType: RecipeType<T>, modifier: IRecipeModifier<T>) {
        if (registered.containsKey(recipeType as RecipeType<Any>)) {
            registered[recipeType]?.add(modifier as IRecipeModifier<Any>)
        } else {
            registered.put(recipeType, mutableListOf(modifier as IRecipeModifier<Any>))
        }
    }
}