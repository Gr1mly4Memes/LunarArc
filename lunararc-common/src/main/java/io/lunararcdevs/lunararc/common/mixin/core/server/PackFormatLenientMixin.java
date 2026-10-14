package io.lunararcdevs.lunararc.common.mixin.core.server;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.util.InclusiveRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackFormat.IntermediaryFormat.class)
public abstract class PackFormatLenientMixin {
    @Shadow public abstract Optional<PackFormat> min();
    @Shadow public abstract Optional<PackFormat> max();
    @Shadow public abstract Optional<Integer> format();
    @Shadow public abstract Optional<InclusiveRange<Integer>> supported();

    @ModifyReturnValue(method = "validate", at = @At("RETURN"), require = 1)
    private DataResult<InclusiveRange<PackFormat>> lunararc$acceptDeclaredFormat(DataResult<InclusiveRange<PackFormat>> original) {
        if (original.result().isPresent()) return original;
        if (min().isPresent() && max().isPresent() && supported().isEmpty() && min().get().compareTo(max().get()) <= 0) {
            return DataResult.success(new InclusiveRange<>(min().get(), max().get()));
        }
        if (min().isEmpty() && max().isEmpty() && format().isPresent()) {
            return DataResult.success(new InclusiveRange<>(PackFormat.of(format().get())));
        }
        return original;
    }
}
