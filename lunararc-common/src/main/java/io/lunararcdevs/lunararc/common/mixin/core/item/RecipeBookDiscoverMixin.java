package io.lunararcdevs.lunararc.common.mixin.core.item;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(ServerRecipeBook.class)
public abstract class RecipeBookDiscoverMixin {
    @WrapMethod(method = "addRecipes", require = 0)
    private int lunararc$discover(Collection<RecipeHolder<?>> recipes, ServerPlayer player, Operation<Integer> original) {
        List<RecipeHolder<?>> allowed = new ArrayList<>(recipes.size());
        ServerRecipeBook book = (ServerRecipeBook) (Object) this;
        for (RecipeHolder<?> holder : recipes) {
            if (book.contains(holder.id()) || LunarArcMoreEvents.recipeDiscover(player, holder.id().identifier())) allowed.add(holder);
        }
        return original.call(allowed, player);
    }
}
