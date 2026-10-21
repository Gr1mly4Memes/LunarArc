package io.lunararcdevs.lunararc.common.mixin.core.patched;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

/** Members Paper adds to MinecraftServer that donated Paper classes (config, profile cache) read directly. */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerPaperMembersMixin {
    /** Paper's async-chat pool; its configuration post-processing resizes it. */
    public ExecutorService chatExecutor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            runnable -> {
                Thread thread = new Thread(runnable, "Async Chat Thread");
                thread.setDaemon(true);
                return thread;
            });

    /** Ticks between automatic saves, as Paper stores it for its fallback configuration values. */
    public int autosavePeriod = 6000;

    public boolean hasStopped() {
        return ((MinecraftServer) (Object) this).isStopped();
    }
}
