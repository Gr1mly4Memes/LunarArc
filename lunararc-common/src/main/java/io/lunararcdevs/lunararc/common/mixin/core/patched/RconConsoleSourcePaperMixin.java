package io.lunararcdevs.lunararc.common.mixin.core.patched;

import java.net.SocketAddress;
import net.minecraft.server.rcon.RconConsoleSource;
import org.spongepowered.asm.mixin.Mixin;

/** Paper records which address an rcon console command came from. */
@Mixin(RconConsoleSource.class)
public abstract class RconConsoleSourcePaperMixin {
    public SocketAddress socketAddress;
}
