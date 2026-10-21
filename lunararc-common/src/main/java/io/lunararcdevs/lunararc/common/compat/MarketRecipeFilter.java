package io.lunararcdevs.lunararc.common.compat;

import com.google.gson.JsonElement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.Identifier;

public final class MarketRecipeFilter {
    private MarketRecipeFilter() {}

    public static Map<Identifier, JsonElement> filter(Map<Identifier, JsonElement> recipes,
            Predicate<Identifier> itemExists) {
        Map<Identifier, JsonElement> filtered = new LinkedHashMap<>(recipes);
        filtered.entrySet().removeIf(entry -> {
            Identifier id = entry.getKey();
            if (!id.getNamespace().equals("farmingforblockheads") || !id.getPath().startsWith("market/")) return false;
            if (!entry.getValue().isJsonObject()) return false;
            var recipe = entry.getValue().getAsJsonObject();
            if (!recipe.has("type") || !recipe.get("type").isJsonPrimitive()
                    || !"farmingforblockheads:market".equals(recipe.get("type").getAsString())) return false;
            if (!recipe.has("result") || !recipe.get("result").isJsonObject()) return false;
            var result = recipe.getAsJsonObject("result");
            if (!result.has("item") || !result.get("item").isJsonPrimitive()
                    || !result.getAsJsonPrimitive("item").isString()) return false;
            Identifier item = Identifier.tryParse(result.get("item").getAsString());
            return item != null && !itemExists.test(item);
        });
        return filtered;
    }
}
