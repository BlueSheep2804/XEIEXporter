package dev.bluesheep.xeiexporter.exporter.resources

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.registries.ForgeRegistries
import java.io.IOException

class FluidRenderer : IRenderer {
    override val name: String = "fluids"
    override val entries: Map<ResourceLocation, NativeImage>
        get() = mapOf(*ForgeRegistries.FLUIDS.entries
            .map { (key, value) ->
                // TintColorを適用させる
                val fluidExtension = IClientFluidTypeExtensions.of(value)
                val textureLocation = fluidExtension.stillTexture
                    ?.withPrefix("textures/")
                    ?.withSuffix(".png")

                val baseTexture = try {
                    if (textureLocation != null) {
                        val texture = Minecraft.getInstance().resourceManager.open(textureLocation)
                        NativeImage.read(texture)
                    } else null
                } catch (e: IOException) {
                    null
                }
                val texture = NativeImage(16, 16, true)
                baseTexture?.copyRect(
                    texture,
                    0, 0,
                    0, 0,
                    16, 16,
                    false, false
                )
                baseTexture?.close()
                return@map key.location() to texture
            }.toTypedArray())
}