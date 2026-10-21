package io.lunararcdevs.lunararc.neoforge.bridge;

import java.util.Map;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistration;

public final class NeoForgeNetworkRegistryBridge {
    private static volatile Map<ConnectionProtocol, Map<Identifier, PayloadRegistration<?>>> payloadRegistrations;
    private static volatile Map<Identifier, StreamCodec<FriendlyByteBuf, ? extends CustomPacketPayload>> builtinPayloads;

    private static volatile Map<ConnectionProtocol, Map<Identifier, IPayloadHandler<?>>> serverboundHandlers;

    private NeoForgeNetworkRegistryBridge() {}

    public static void install(
            Map<ConnectionProtocol, Map<Identifier, PayloadRegistration<?>>> registrations,
            Map<Identifier, StreamCodec<FriendlyByteBuf, ? extends CustomPacketPayload>> builtins,
            Map<ConnectionProtocol, Map<Identifier, IPayloadHandler<?>>> serverbound) {
        serverboundHandlers = java.util.Objects.requireNonNull(serverbound, "serverbound");
        payloadRegistrations = java.util.Objects.requireNonNull(registrations, "registrations");
        builtinPayloads = java.util.Objects.requireNonNull(builtins, "builtins");
    }

    public static Map<ConnectionProtocol, Map<Identifier, PayloadRegistration<?>>> payloadRegistrations() {
        Map<ConnectionProtocol, Map<Identifier, PayloadRegistration<?>>> value = payloadRegistrations;
        if (value == null) throw new IllegalStateException("NeoForge payload registry bridge was not initialized");
        return value;
    }

    public static Map<Identifier, StreamCodec<FriendlyByteBuf, ? extends CustomPacketPayload>> builtinPayloads() {
        Map<Identifier, StreamCodec<FriendlyByteBuf, ? extends CustomPacketPayload>> value = builtinPayloads;
        if (value == null) throw new IllegalStateException("NeoForge builtin payload bridge was not initialized");
        return value;
    }

    public static Map<ConnectionProtocol, Map<Identifier, IPayloadHandler<?>>> serverboundHandlers() {
        Map<ConnectionProtocol, Map<Identifier, IPayloadHandler<?>>> value = serverboundHandlers;
        if (value == null) throw new IllegalStateException("NeoForge payload handler bridge was not initialized");
        return value;
    }
}
