package dev.bluesheep.xeiexporter.api.recipe

interface IRecipeStackData {
    val type: String
    val entry: String
    val uniqueId: String
    val amount: Long
    val chance: Float
}