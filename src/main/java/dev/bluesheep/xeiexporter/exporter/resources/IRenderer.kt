package dev.bluesheep.xeiexporter.exporter.resources

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.resources.ResourceLocation

interface IRenderer {
    val name: String
    val entries: Map<ResourceLocation, NativeImage>
}