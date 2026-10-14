package io.lunararcdevs.lunararc.common.mixin.core.world.raid;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import io.lunararcdevs.lunararc.common.bridge.world.raid.RaidBridge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.raid.Raids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Saves and restores a raid's Bukkit PersistentDataContainer next to the raid itself, as Paper does. */
@Mixin(value = Raids.RaidWithId.class, remap = false)
public abstract class RaidPersistentDataMixin {
    private static final String LUNARARC_KEY = "BukkitValues";

    @Shadow @Mutable public static Codec<Raids.RaidWithId> CODEC;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void lunararc$persistentData(CallbackInfo ci) {
        Codec<Raids.RaidWithId> base = CODEC;
        CODEC = Codec.of(new Encoder<Raids.RaidWithId>() {
            @Override
            public <T> DataResult<T> encode(Raids.RaidWithId input, DynamicOps<T> ops, T prefix) {
                DataResult<T> encoded = base.encode(input, ops, prefix);
                CompoundTag data = ((RaidBridge) input.raid()).lunararc$persistentData().toTagCompound();
                if (data.isEmpty()) return encoded;
                return encoded.flatMap(value -> CompoundTag.CODEC.encodeStart(ops, data)
                        .flatMap(tag -> ops.mergeToMap(value, ops.createString(LUNARARC_KEY), tag)));
            }
        }, new Decoder<Raids.RaidWithId>() {
            @Override
            public <T> DataResult<Pair<Raids.RaidWithId, T>> decode(DynamicOps<T> ops, T input) {
                return base.decode(ops, input).map(pair -> {
                    ops.get(input, LUNARARC_KEY).result()
                            .flatMap(value -> CompoundTag.CODEC.parse(ops, value).result())
                            .ifPresent(tag -> ((RaidBridge) pair.getFirst().raid()).lunararc$persistentData().putAll(tag));
                    return pair;
                });
            }
        });
    }
}
