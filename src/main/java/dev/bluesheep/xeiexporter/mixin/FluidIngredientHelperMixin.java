package dev.bluesheep.xeiexporter.mixin;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.library.plugins.vanilla.ingredients.fluid.FluidIngredientHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * FluidStackのサブタイプの有無を修正するMixin
 * */
@Mixin(FluidIngredientHelper.class)
public abstract class FluidIngredientHelperMixin<T> implements IIngredientHelper<T> {
    @Shadow @Final private ISubtypeManager subtypeManager;

    @Shadow @Final private IPlatformFluidHelperInternal<T> platformFluidHelper;

    @Override
    public boolean hasSubtypes(@NotNull T ingredient) {
        return subtypeManager.hasSubtypes(platformFluidHelper.getFluidIngredientType(), ingredient);
    }
}
