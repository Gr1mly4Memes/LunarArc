package io.lunararcdevs.lunararc.common.mixin.core.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.saveddata.SavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(SavedData.class)
public abstract class SavedDataDirectoryMixin {
    @Inject(method = "save(Ljava/io/File;Lnet/minecraft/core/HolderLookup$Provider;)V", at = @At("HEAD"), require = 0)
    private void lunararc$ensureDataDirectory(File file, HolderLookup.Provider registries, CallbackInfo ci) {
        File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory()) parent.mkdirs();
    }
}
