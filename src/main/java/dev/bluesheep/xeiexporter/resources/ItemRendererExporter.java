package dev.bluesheep.xeiexporter.resources;

import dev.bluesheep.xeiexporter.XEIExporter;
import dev.bluesheep.xeiexporter.ExportUtil;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.joml.Matrix4f;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemRendererExporter {
    private static final Path EXPORT_ITEM_RENDERER_DIR = XEIExporter.EXPORT_ASSETS_DIR.resolve("items");
    private static final int exportCount = 16;
    private static final Ingredient skullIngredient = Ingredient.of(
            Items.SKELETON_SKULL,
            Items.WITHER_SKELETON_SKULL,
            Items.CREEPER_HEAD,
            Items.PIGLIN_HEAD,
            Items.DRAGON_HEAD,
            Items.PLAYER_HEAD,
            Items.ZOMBIE_HEAD
    );

    private final ArrayList<Map.Entry<ResourceKey<Item>, Item>> pendingItemList = new ArrayList<>();
    private int itemCount = 0;

    public ItemRendererExporter() {}

    public void addItem(Map.Entry<ResourceKey<Item>, Item> itemEntry) {
        pendingItemList.add(itemEntry);
        itemCount++;

        if (pendingItemList.size() > exportCount - 1) {
            exportItemIcon();
        }
    }

    private void exportItemIcon() {
        Map<ResourceLocation, NativeImage> imageMap = renderItems();
        Util.ioPool().execute(() -> imageMap.forEach(this::saveItems));
        pendingItemList.clear();
    }

    public void end() {
        exportItemIcon();
    }

    private Map<ResourceLocation, NativeImage> renderItems() {
        Minecraft minecraft = Minecraft.getInstance();

        RenderTarget renderTarget = new TextureTarget(64 * exportCount, 64, true, Minecraft.ON_OSX);
        renderTarget.bindWrite(true);
        RenderSystem.clear(256, Minecraft.ON_OSX);

        Matrix4f matrix4f = new Matrix4f().setOrtho(
                0.0f,
                16.0f * exportCount,
                16.0f,
                0.0f,
                1000.0f,
                3000.0f
        );
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);

        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.setIdentity();
        poseStack.translate(0.0, 0.0, -2000.0);
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();

        GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
        for (int i = 0; i < pendingItemList.size(); i++) {
            ItemStack item = new ItemStack(pendingItemList.get(i).getValue());
            if (!skullIngredient.test(item)) {
                poseStack.pushPose();
                poseStack.scale(2.0f, 2.0f, 2.0f);
            }
            guiGraphics.renderItem(new ItemStack(pendingItemList.get(i).getValue()), 16 * i, 0);
            if (!skullIngredient.test(item)) {
                poseStack.popPose();
            }
        }

        NativeImage nativeImage = new NativeImage(64 * exportCount, 64, true);
        RenderSystem.bindTexture(renderTarget.getColorTextureId());
        nativeImage.downloadTexture(0, false);
        nativeImage.flipY();

        Map<ResourceLocation, NativeImage> imageMap = new HashMap<>();
        for (int i = 0; i < pendingItemList.size(); i++) {
            NativeImage image = new NativeImage(64, 64, false);
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
            );
            imageMap.put(pendingItemList.get(i).getKey().location(), image);
        }

//        saveItems(new ResourceLocation("temp", Integer.toString(itemCount)), nativeImage);
        nativeImage.close();

        renderTarget.destroyBuffers();
        minecraft.levelRenderer.graphicsChanged();
        minecraft.getMainRenderTarget().bindWrite(true);

        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();

        return imageMap;
    }

    private void saveItems(ResourceLocation itemId, NativeImage nativeImage) {
        try {
            nativeImage.writeToFile(ExportUtil.resourceLocationToPath(EXPORT_ITEM_RENDERER_DIR, itemId, ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            nativeImage.close();
        }
    }
}
