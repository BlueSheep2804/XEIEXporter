package dev.bluesheep.xeiexporter.api.renderer

import com.mojang.blaze3d.platform.NativeImage

interface IRenderer {
    val name: String
    val entries: Map<String, NativeImage>
}