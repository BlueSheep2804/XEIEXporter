package dev.bluesheep.xeiexporter.api.recipe.ingredient

class EmptyRecipeIngredient : AbstractRecipeIngredient<String>("") {
    override val entryType: String = ""

    override fun export(): String {
        return ""
    }
}