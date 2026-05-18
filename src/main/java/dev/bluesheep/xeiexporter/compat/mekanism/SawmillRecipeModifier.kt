package dev.bluesheep.xeiexporter.compat.mekanism

import dev.bluesheep.xeiexporter.api.XEIExporterApi
import dev.bluesheep.xeiexporter.api.recipe.IRecipeData
import dev.bluesheep.xeiexporter.api.recipe.IRecipeModifier
import mekanism.api.recipes.SawmillRecipe

class SawmillRecipeModifier : IRecipeModifier<SawmillRecipe> {
    override fun modify(recipe: SawmillRecipe, current: IRecipeData, default: IRecipeData): IRecipeData {
        if (recipe.secondaryOutputDefinition.isEmpty()) return current

        val output = current.output.toMutableList()
        output[1] = output[1].map {
            XEIExporterApi.INSTANCE.createRecipeStackData(
                it.type,
                it.entry,
                amount = it.amount,
                chance = recipe.secondaryChance.toFloat()
            )
        }

        return XEIExporterApi.INSTANCE.createRecipeData(
            current.id,
            current.type,
            current.input,
            output.toList()
        )
    }
}