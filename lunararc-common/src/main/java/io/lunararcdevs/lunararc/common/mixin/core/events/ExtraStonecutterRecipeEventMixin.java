package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** PlayerStonecutterRecipeSelectEvent: a plugin may cancel the selection or swap the chosen recipe. */
@Mixin(StonecutterMenu.class)
public abstract class ExtraStonecutterRecipeEventMixin {
    @Shadow private SelectableRecipe.SingleInputSet<StonecutterRecipe> recipesForInput;

    @WrapMethod(method = "clickMenuButton", require = 0)
    private boolean lunararc$recipeSelected(Player player, int id, Operation<Boolean> original) {
        if (id < 0 || id >= this.recipesForInput.size()) return original.call(player, id);
        List<Optional<RecipeHolder<StonecutterRecipe>>> all = new ArrayList<>();
        for (SelectableRecipe.SingleInputEntry<StonecutterRecipe> entry : this.recipesForInput.entries()) {
            all.add(entry.recipe().recipe());
        }
        RecipeHolder<StonecutterRecipe> chosen = all.get(id).orElse(null);
        int index = LunarArcExtraEvents.stonecutterRecipe(player, (AbstractContainerMenu) (Object) this, id, chosen, all);
        if (index == -2) {
            player.containerMenu.sendAllDataToRemote();
            return false;
        }
        return original.call(player, index);
    }
}
