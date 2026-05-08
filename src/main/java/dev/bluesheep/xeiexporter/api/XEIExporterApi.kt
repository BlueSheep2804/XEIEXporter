package dev.bluesheep.xeiexporter.api

import dev.bluesheep.xeiexporter.api.recipe.IRecipeData
import dev.bluesheep.xeiexporter.api.recipe.IRecipeStackData
import net.minecraft.resources.ResourceLocation

abstract class XEIExporterApi {
    companion object {
        private lateinit var internalInstance: XEIExporterApi

        var INSTANCE: XEIExporterApi
            get() = internalInstance
            internal set(value) {
                if (!isInitialized) {
                    internalInstance = value
                }
            }

        @JvmStatic
        val isInitialized
            get() = this::internalInstance.isInitialized
    }

    abstract fun createRecipeData(
        id: ResourceLocation,
        type: ResourceLocation,
        input: List<List<IRecipeStackData>>,
        output: List<List<IRecipeStackData>>
    ): IRecipeData

    abstract fun createRecipeStackData(
        type: String,
        entry: String,
        uniqueId: String = "",
        amount: Long = 1,
        chance: Float = 1f
    ): IRecipeStackData
}