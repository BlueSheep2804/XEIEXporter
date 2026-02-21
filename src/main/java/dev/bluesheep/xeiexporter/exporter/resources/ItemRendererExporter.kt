package dev.bluesheep.xeiexporter.exporter.resources

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexSorting
import dev.bluesheep.xeiexporter.exporter.ExportUtil.resourceLocationToPath
import dev.bluesheep.xeiexporter.XEIExporter
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import org.joml.Matrix4f
import java.io.IOException
import java.nio.file.Path

class ItemRendererExporter {
    companion object {
        private val EXPORT_ITEM_RENDERER_DIR: Path = XEIExporter.EXPORT_ASSETS_DIR.resolve("items")
        private const val EXPORT_COUNT = 16
        private val skullIngredient: Ingredient = Ingredient.of(
            Items.SKELETON_SKULL,
            Items.WITHER_SKELETON_SKULL,
            Items.CREEPER_HEAD,
            Items.PIGLIN_HEAD,
            Items.DRAGON_HEAD,
            Items.PLAYER_HEAD,
            Items.ZOMBIE_HEAD
        )
    }

    private val pendingItemList = mutableListOf<MutableMap.MutableEntry<ResourceKey<Item>, Item>>()
    private var itemCount = 0

    fun addItem(itemEntry: MutableMap.MutableEntry<ResourceKey<Item>, Item>) {
        pendingItemList.add(itemEntry)
        itemCount++

        if (pendingItemList.size > EXPORT_COUNT - 1) {
            exportItemIcon()
        }
    }

    private fun exportItemIcon() {
        val imageMap = renderItems()
        Util.ioPool().execute {
            imageMap.forEach(::saveItems)
        }
        pendingItemList.clear()
    }

    fun end() {
        exportItemIcon()
    }

    private fun renderItems(): Map<ResourceLocation, NativeImage> {
        val minecraft = Minecraft.getInstance()

        val renderTarget: RenderTarget = TextureTarget(64 * EXPORT_COUNT, 64, true, Minecraft.ON_OSX)
        renderTarget.bindWrite(true)
        RenderSystem.clear(256, Minecraft.ON_OSX)

        val matrix4f = Matrix4f().setOrtho(
            0.0f,
            16.0f * EXPORT_COUNT,
            16.0f,
            0.0f,
            1000.0f,
            3000.0f
        )
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z)

        val poseStack = RenderSystem.getModelViewStack()
        poseStack.pushPose()
        poseStack.setIdentity()
        poseStack.translate(0.0, 0.0, -2000.0)
        RenderSystem.applyModelViewMatrix()
        Lighting.setupFor3DItems()

        val guiGraphics = GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource())
        for (i in pendingItemList.indices) {
            val item = ItemStack(pendingItemList[i].value)
            if (!skullIngredient.test(item)) {
                poseStack.pushPose()
                poseStack.scale(2.0f, 2.0f, 2.0f)
            }
            guiGraphics.renderItem(ItemStack(pendingItemList[i].value), 16 * i, 0)
            if (!skullIngredient.test(item)) {
                poseStack.popPose()
            }
        }

        val nativeImage = NativeImage(64 * EXPORT_COUNT, 64, true)
        RenderSystem.bindTexture(renderTarget.getColorTextureId())
        nativeImage.downloadTexture(0, false)
        nativeImage.flipY()

        val imageMap: MutableMap<ResourceLocation, NativeImage> = mutableMapOf()
        for (i in pendingItemList.indices) {
            val image = NativeImage(64, 64, false)
            nativeImage.copyRect(
                image,
                i * 64,
                0,
                0,
                0,
                64,
                64,
                false,
                false
            )
            imageMap.put(pendingItemList[i].key.location(), image)
        }

        nativeImage.close()

        renderTarget.destroyBuffers()
        minecraft.levelRenderer.graphicsChanged()
        minecraft.mainRenderTarget.bindWrite(true)

        poseStack.popPose()
        RenderSystem.applyModelViewMatrix()

        return imageMap
    }

    private fun saveItems(itemId: ResourceLocation, nativeImage: NativeImage) {
        try {
            nativeImage.writeToFile(resourceLocationToPath(EXPORT_ITEM_RENDERER_DIR, itemId, ".png"))
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            nativeImage.close()
        }
    }
}
