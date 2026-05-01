package dev.bluesheep.xeiexporter.api.recipe

interface IRecipeStackData {
    val type: String
    val entry: String
    val amount: Int
    val chance: Float
}