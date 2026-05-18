package dev.bluesheep.xeiexporter.compat.mekanism

import dev.bluesheep.xeiexporter.api.event.RegisterIngredientExtraHelperEvent
import dev.bluesheep.xeiexporter.api.event.RegisterRecipeModifierEvent
import dev.bluesheep.xeiexporter.api.event.RegisterTagEvent
import mekanism.api.MekanismAPI
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.infuse.InfusionStack
import mekanism.api.chemical.pigment.PigmentStack
import mekanism.api.chemical.slurry.SlurryStack
import mekanism.client.jei.MekanismJEI
import mekanism.client.jei.MekanismJEIRecipeType
import net.minecraftforge.eventbus.api.SubscribeEvent

object MekanismEventHandler {
    @SubscribeEvent
    fun registerIngredient(event: RegisterIngredientExtraHelperEvent) {
        event.register(
            { GasStack::class.java },
            ChemicalIngredientExtraHelper("transmission.mekanism.gases")
        )
        event.register(
            { SlurryStack::class.java },
            ChemicalIngredientExtraHelper("transmission.mekanism.slurries")
        )
        event.register(
            { PigmentStack::class.java },
            ChemicalIngredientExtraHelper("transmission.mekanism.pigments")
        )
        event.register(
            { InfusionStack::class.java },
            ChemicalIngredientExtraHelper("transmission.mekanism.infuse_types")
        )
    }

    @SubscribeEvent
    fun registerRecipeModifier(event: RegisterRecipeModifierEvent) {
        event.register(
            MekanismJEI.recipeType(MekanismJEIRecipeType.SAWING),
            SawmillRecipeModifier()
        )
    }

    @SubscribeEvent
    fun registerTag(event: RegisterTagEvent) {
        event.register(MekanismJEI.TYPE_GAS, MekanismAPI.gasRegistry())
        event.register(MekanismJEI.TYPE_PIGMENT, MekanismAPI.pigmentRegistry())
        event.register(MekanismJEI.TYPE_SLURRY, MekanismAPI.slurryRegistry())
        event.register(MekanismJEI.TYPE_INFUSION, MekanismAPI.infuseTypeRegistry())
    }
}