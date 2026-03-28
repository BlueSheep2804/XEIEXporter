package dev.bluesheep.xeiexporter.mixin;

import dev.bluesheep.xeiexporter.exporter.recipe.JEIRecipeHandler;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.gui.recipes.RecipeLayout;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Optional;

@Mixin(value = RecipeLayout.class, remap = false)
public class RecipeLayoutMixin {
    @Inject(method = "create(Lmezz/jei/api/recipe/category/IRecipeCategory;Ljava/util/Collection;Ljava/lang/Object;Lmezz/jei/api/recipe/IFocusGroup;Lmezz/jei/api/runtime/IIngredientManager;Lmezz/jei/api/gui/drawable/IScalableDrawable;I)Ljava/util/Optional;", at = @At("RETURN"))
    private static <T> void createInjection(IRecipeCategory<T> recipeCategory, Collection<IRecipeCategoryDecorator<T>> decorators, T recipe, IFocusGroup focuses, IIngredientManager ingredientManager, IScalableDrawable recipeBackground, int recipeBorderPadding, CallbackInfoReturnable<Optional<IRecipeLayoutDrawable<T>>> cir) {
        if (cir.getReturnValue().isPresent()) {
            IRecipeLayoutDrawable<T> recipeLayout = cir.getReturnValue().get();
            ResourceLocation recipeId = recipeLayout.getRecipeCategory().getRegistryName(
                    recipeLayout.getRecipe()
            );
            JEIRecipeHandler.addRecipe(recipeId, recipeLayout);
        }
    }
}
