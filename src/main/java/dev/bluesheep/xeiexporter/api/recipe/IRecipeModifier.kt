package dev.bluesheep.xeiexporter.api.recipe

interface IRecipeModifier<T> {
    fun modify(recipe: T, current: IRecipeData, default: IRecipeData): IRecipeData {
        return current
    }
}