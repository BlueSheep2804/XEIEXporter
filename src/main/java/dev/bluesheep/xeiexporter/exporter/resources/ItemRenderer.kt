package dev.bluesheep.xeiexporter.exporter.resources

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexSorting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries
import org.joml.Matrix4f

class ItemRenderer : IRenderer {
    override val name: String = "items"

    override val entries: Map<ResourceLocation, NativeImage>
        get() = render(ForgeRegistries.ITEMS.entries)

    fun render(entries: Set<Map.Entry<ResourceKey<Item>, Item>>): Map<ResourceLocation, NativeImage> {
        val minecraft = Minecraft.getInstance()

        val renderTarget: RenderTarget = TextureTarget(64, 64, true, Minecraft.ON_OSX)
        renderTarget.bindWrite(true)

        val matrix4f = Matrix4f().setOrtho(
            0.0f,
            16.0f,
            16.0f,
            0.0f,
            1000.0f,
            3000.0f
        )

        val imageMap: MutableMap<ResourceLocation, NativeImage> = mutableMapOf()
        entries.forEach { (key, value) ->
            RenderSystem.clear(16640, Minecraft.ON_OSX)

            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z)

            val poseStack = RenderSystem.getModelViewStack()
            poseStack.pushPose()
            poseStack.setIdentity()
            poseStack.translate(0.0, 0.0, -2000.0)
            RenderSystem.applyModelViewMatrix()
            Lighting.setupFor3DItems()

            val guiGraphics = GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource())

            val itemStack = ItemStack(value)
            guiGraphics.renderItem(itemStack, 0, 0)

            guiGraphics.flush()

            val nativeImage = NativeImage(64, 64, true)
            RenderSystem.bindTexture(renderTarget.colorTextureId)
            nativeImage.downloadTexture(0, false)
            nativeImage.flipY()

            imageMap.put(key.location(), nativeImage)

            poseStack.popPose()
            RenderSystem.applyModelViewMatrix()
        }

        renderTarget.destroyBuffers()
        minecraft.levelRenderer.graphicsChanged()
        minecraft.mainRenderTarget.bindWrite(true)

        return imageMap
    }
}