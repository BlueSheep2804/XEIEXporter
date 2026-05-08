package dev.bluesheep.xeiexporter

import dev.bluesheep.xeiexporter.api.XEIExporterApi
import dev.bluesheep.xeiexporter.api.recipe.IRecipeData
import dev.bluesheep.xeiexporter.api.recipe.IRecipeStackData
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeStackData
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeData
import net.minecraft.resources.ResourceLocation

object ApiImpl : XEIExporterApi() {
    fun init() {
        XEIExporterApi.INSTANCE = this
    }

    override fun createRecipeData(
        id: ResourceLocation,
        type: ResourceLocation,
        input: List<List<IRecipeStackData>>,
        output: List<List<IRecipeStackData>>
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
        uniqueId: String,
        amount: Long,
        chance: Float
    ): IRecipeStackData {
        return RecipeStackData(
            type,
            entry,
            uniqueId,
            amount,
            chance
        )
    }
}