package dev.bluesheep.xeiexporter.api

import dev.bluesheep.xeiexporter.api.recipe.IRecipeData
import dev.bluesheep.xeiexporter.api.recipe.IRecipeStackData
import dev.bluesheep.xeiexporter.exporter.recipe.RecipeStackData
import net.minecraft.resources.ResourceLocation

abstract class XEIExporterApi {
    companion object {
        private lateinit var INSTANCE: XEIExporterApi

        internal fun setInstance(instance: XEIExporterApi) {
            if (!isInitialized) {
                INSTANCE = instance
            }
        }

        @JvmStatic
        val isInitialized
            get() = this::INSTANCE.isInitialized
    }

    abstract fun createRecipeData(
        id: ResourceLocation,
        type: ResourceLocation,
        input: List<List<RecipeStackData>>,
        output: List<List<RecipeStackData>>
    ): IRecipeData

    abstract fun createRecipeStackData(
        type: String,
        entry: String,
        uniqueId: String = "",
        amount: Long = 1,
        chance: Float = 1f
    ): IRecipeStackData
}