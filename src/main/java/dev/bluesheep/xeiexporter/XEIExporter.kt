package dev.bluesheep.xeiexporter

import com.mojang.logging.LogUtils
import dev.bluesheep.xeiexporter.api.recipe.RecipeData
import dev.bluesheep.xeiexporter.api.recipe.ingredient.ItemRecipeIngredient
import dev.bluesheep.xeiexporter.api.recipe.result.ItemRecipeResult
import dev.bluesheep.xeiexporter.debug.DebugRegister
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import dev.bluesheep.xeiexporter.exporter.recipe.JEIRecipeHandler
import mezz.jei.api.recipe.RecipeIngredientRole
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.network.chat.Component
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.loading.FMLLoader
import net.minecraftforge.fml.loading.FMLPaths
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT
import java.nio.file.Path

@Mod(value = XEIExporter.MODID)
object XEIExporter {
    const val MODID: String = "xeiexporter"
    @JvmField
    val LOGGER: Logger = LogUtils.getLogger()

    @JvmField
    val EXPORT_DIR: Path = FMLPaths.GAMEDIR.get().resolve(MODID)
    @JvmField
    val EXPORT_ASSETS_DIR: Path = EXPORT_DIR.resolve("assets")

    init {
        val modEventBus = MOD_CONTEXT.getKEventBus()

        FORGE_BUS.register(this)

        if (!FMLLoader.isProduction()) {
            DebugRegister.BLOCKS.register(modEventBus)
            DebugRegister.ITEMS.register(modEventBus)
            DebugRegister.CREATIVE_MODE_TABS.register(modEventBus)
        }

        LOADING_CONTEXT.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    @SubscribeEvent
    fun register(event: RegisterCommandsEvent) {
        event.dispatcher.register(
            Commands.literal("export").executes { context ->
                return@executes ExportUtil.export()
            }
        )

        event.dispatcher.register(
            Commands.literal("export_recipes")
                .then(Commands.argument("recipe_type", ResourceLocationArgument.id()).executes { context ->
                    if (context.source.isPlayer) {
                        context.nodes.forEach { context.source.player?.sendSystemMessage(Component.literal(it.range.get(context.input))) }
                    }

                    val recipeType = ResourceLocationArgument.getId(context, "recipe_type")
                    val recipes = mutableListOf<RecipeData>()
                    JEIRecipeHandler.getRecipes(recipeType).forEach { recipeId, recipeLayout ->
                        recipes.add(RecipeData(
                            recipeId,
                            recipeType,
                            recipeLayout.recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).map {
                                ItemRecipeIngredient(it.itemStacks.toList())
                            },
                            recipeLayout.recipeSlotsView.getSlotViews(RecipeIngredientRole.OUTPUT).map {
                                ItemRecipeResult(it.itemStacks.toList().first())
                            }
                        ))
                    }

                    return@executes 0
                })
        )
    }

    @EventBusSubscriber(modid = MODID, value = [Dist.CLIENT])
    object ClientModEvents {
        @SubscribeEvent
        fun registerClientCommand(event: RegisterClientCommandsEvent) {
            event.dispatcher.register(
                Commands.literal("exportc").executes { context ->
                    return@executes ExportUtil.exportClient()
                }
            )
        }
    }
}
