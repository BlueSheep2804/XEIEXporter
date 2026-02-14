package dev.bluesheep.xeiexporter;

import dev.bluesheep.xeiexporter.resources.ItemRendererExporter;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ItemExporter {
    private static final Path EXPORT_ITEM_FILE = XEIExporter.EXPORT_DIR.resolve("items.json");
    private static final Path EXPORT_ITEMS_DIR = XEIExporter.EXPORT_DIR.resolve("items");

    public static void exportItems() {
        var itemsEntries = ForgeRegistries.ITEMS.getEntries();
        ExportUtil.mkdir(EXPORT_ITEMS_DIR);
        itemsEntries.forEach(ItemExporter::exportItem);

        ItemRendererExporter itemRendererExporter = new ItemRendererExporter();
        itemsEntries.forEach(itemRendererExporter::addItem);
        itemRendererExporter.end();

        List<String> itemList = itemsEntries.stream().map(it -> it.getKey().location().toString()).toList();
        ExportUtil.saveExportFile(itemList, EXPORT_ITEM_FILE);
    }

    private static void exportItem(Map.Entry<ResourceKey<Item>, Item> itemEntry) {
        Item item = itemEntry.getValue();
        ResourceLocation itemId = itemEntry.getKey().location();
        ItemStack stack = new ItemStack(item);

        JsonObject json = new JsonObject();
        json.addProperty("id", itemId.toString());
        json.addProperty("description_id", item.getDescriptionId());
        json.addProperty("max_damage", item.getMaxDamage(stack));
        json.addProperty("rarity", item.getRarity(stack).name().toLowerCase(Locale.US));
        json.addProperty("class", item.getClass().getName());

        ExportUtil.saveExportFile(json, ExportUtil.resourceLocationToJson(EXPORT_ITEMS_DIR, itemId));
    }
}
