package io.lunararcdevs.lunararc.common.mixin.core.server;

import io.lunararcdevs.lunararc.common.server.LunarArcProfileCacheWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import net.minecraft.server.players.CachedUserNameToIdResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CachedUserNameToIdResolver.class)
public abstract class GameProfileCacheMixin {
    @Redirect(method = "save", require = 0, at = @At(value = "INVOKE", target = "Lcom/google/common/io/Files;newWriter(Ljava/io/File;Ljava/nio/charset/Charset;)Ljava/io/BufferedWriter;", remap = false))
    private BufferedWriter lunararc$deferProfileWrite(File file, Charset charset) {
        return LunarArcProfileCacheWriter.open(file, charset);
    }
}
