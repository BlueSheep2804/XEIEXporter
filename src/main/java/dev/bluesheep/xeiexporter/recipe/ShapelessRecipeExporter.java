package dev.bluesheep.xeiexporter.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ShapelessRecipeExporter {
    ShapelessRecipeExporter() {}

    public JsonObject export(ShapelessRecipe recipe, Level level) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");
        json.addProperty("category", recipe.category().getSerializedName());
        if (!recipe.getGroup().isEmpty()) {
            json.addProperty("group", recipe.getGroup());
        }

        JsonArray ingredients = new JsonArray();
        recipe.getIngredients().forEach(it -> ingredients.add(it.toJson()));
        json.add("ingredients", ingredients);

        ItemStack itemStack = recipe.getResultItem(level.registryAccess());
        JsonObject result = new JsonObject();
        result.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemStack.getItem())).toString());
        result.addProperty("count", itemStack.getCount());
        CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, itemStack.getTag())
                .result()
                .ifPresent(tag -> result.add("tag", tag));
        json.add("result", result);

        return json;
    }
}
