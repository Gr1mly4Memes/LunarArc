package io.lunararcdevs.lunararc.common.bridge.access;

import net.minecraft.sounds.SoundEvent;

/** Normal runtime bridge implemented by the corresponding Mixin accessor/invoker. */
public interface MobAccessBridge {
    void lunararc$setPersistenceRequired(boolean value);
    net.minecraft.world.entity.DropChances lunararc$getDropChances();
    void lunararc$setDropChances(net.minecraft.world.entity.DropChances value);
    SoundEvent lunararc$invokeGetAmbientSound();
}
