package dev.bluesheep.xeiexporter.debug

import dev.bluesheep.xeiexporter.XEIExporter
import dev.bluesheep.xeiexporter.exporter.ExportUtil.rl
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import java.util.function.Consumer
import java.util.stream.Stream

@EventBusSubscriber(modid = XEIExporter.MODID, bus = EventBusSubscriber.Bus.MOD)
object DebugDataGen {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput

        generator.addProvider(event.includeServer(), DebugRecipeProvider(output))
    }

    private class DebugRecipeProvider(output: PackOutput): RecipeProvider(output) {
        override fun buildRecipes(recipeOutput: Consumer<FinishedRecipe>) {
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.DEBUG_STICK, 2)
                .pattern("A ")
                .pattern(" B")
                .pattern("C ")
                .define('A', Ingredient.fromValues(Stream.of(
                    Ingredient.ItemValue(ItemStack(Items.BARRIER)),
                    Ingredient.TagValue(ItemTags.EMERALD_ORES)
                )))
                .define('B', ItemTags.PICKAXES)
                .define('C', Ingredient.of(Items.STRUCTURE_BLOCK, Items.STRUCTURE_VOID))
                .unlockedBy("ingredient_test", has(Items.DEBUG_STICK))
                .save(recipeOutput, rl("ingredient_test"))
        }
    }
}
