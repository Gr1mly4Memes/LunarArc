package io.lunararcdevs.lunararc.common.mixin.core.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.lunararcdevs.lunararc.common.server.LunarArcProfileServices;
import net.minecraft.server.Services;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Profile lookup and fill events: the services the server talks to Mojang through are wrapped as they are created. */
@Mixin(Services.class)
public abstract class ServerSideProfileServicesMixin {
    @WrapOperation(method = "create", require = 0,
            at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;createProfileRepository()Lcom/mojang/authlib/GameProfileRepository;"))
    private static GameProfileRepository lunararc$repository(YggdrasilAuthenticationService service, Operation<GameProfileRepository> original) {
        return LunarArcProfileServices.wrap(original.call(service));
    }

    @WrapOperation(method = "create", require = 0,
            at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;createMinecraftSessionService()Lcom/mojang/authlib/minecraft/MinecraftSessionService;"))
    private static MinecraftSessionService lunararc$session(YggdrasilAuthenticationService service, Operation<MinecraftSessionService> original) {
        return LunarArcProfileServices.wrap(original.call(service));
    }
}
