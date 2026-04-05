package dev.bluesheep.xeiexporter.exporter.resources

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexSorting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f

abstract class AbstractRenderSystemRenderer : IRenderer {
    override val entries: Map<ResourceLocation, NativeImage>
        get() = renderByRenderSystem()

    protected abstract val renderList: Map<ResourceLocation, (GuiGraphics) -> Unit>

    protected fun renderByRenderSystem(): Map<ResourceLocation, NativeImage> {
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
        renderList.forEach { (key, value) ->
            RenderSystem.clear(16640, Minecraft.ON_OSX)

            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z)

            val poseStack = RenderSystem.getModelViewStack()
            poseStack.pushPose()
            poseStack.setIdentity()
            poseStack.translate(0.0, 0.0, -2000.0)
            RenderSystem.applyModelViewMatrix()
            Lighting.setupFor3DItems()

            val guiGraphics = GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource())

            value.invoke(guiGraphics)

            guiGraphics.flush()

            val nativeImage = NativeImage(64, 64, true)
            RenderSystem.bindTexture(renderTarget.colorTextureId)
            nativeImage.downloadTexture(0, false)
            nativeImage.flipY()

            imageMap.put(key, nativeImage)

            poseStack.popPose()
            RenderSystem.applyModelViewMatrix()
        }

        renderTarget.destroyBuffers()
        minecraft.levelRenderer.graphicsChanged()
        minecraft.mainRenderTarget.bindWrite(true)

        return imageMap
    }
}