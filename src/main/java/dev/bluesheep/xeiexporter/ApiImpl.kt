package dev.bluesheep.xeiexporter

import dev.bluesheep.xeiexporter.api.XEIExporterApi
import dev.bluesheep.xeiexporter.api.recipe.IRecipeData
import dev.bluesheep.xeiexporter.api.recipe.IRecipeStackData
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeStackData
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeData
import net.minecraft.resources.ResourceLocation

object ApiImpl : XEIExporterApi() {
    fun init() {
        XEIExporterApi.setInstance(this)
    }

    override fun createRecipeData(
        id: ResourceLocation,
        type: ResourceLocation,
        input: List<List<RecipeStackData>>,
        output: List<List<RecipeStackData>>
    ): IRecipeData {
        return RecipeData(
            id,
            type,
            input,
            output
        )
    }

    override fun createRecipeStackData(
        type: String,
        entry: String,
        amount: Long,
        chance: Float
    ): IRecipeStackData {
        return RecipeStackData(
            type,
            entry,
            amount,
            chance
        )
    }
}