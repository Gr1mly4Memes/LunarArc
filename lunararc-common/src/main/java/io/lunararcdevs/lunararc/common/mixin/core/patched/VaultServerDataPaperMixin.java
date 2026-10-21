package io.lunararcdevs.lunararc.common.mixin.core.patched;

import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Paper's Vault API: add or remove a rewarded player by UUID. */
@Mixin(VaultServerData.class)
public abstract class VaultServerDataPaperMixin {
    @Shadow @Final private Set<UUID> rewardedPlayers;
    @Shadow private void markChanged() { throw new AssertionError(); }

    public boolean addToRewardedPlayers(UUID player) {
        boolean added = this.rewardedPlayers.add(player);
        if (this.rewardedPlayers.size() > 128) {
            java.util.Iterator<UUID> oldest = this.rewardedPlayers.iterator();
            if (oldest.hasNext()) {
                oldest.next();
                oldest.remove();
            }
        }
        this.markChanged();
        return added;
    }

    public boolean removeFromRewardedPlayers(UUID player) {
        if (this.rewardedPlayers.remove(player)) {
            this.markChanged();
            return true;
        }
        return false;
    }
}
