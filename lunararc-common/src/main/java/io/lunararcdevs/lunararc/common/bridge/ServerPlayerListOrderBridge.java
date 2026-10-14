package io.lunararcdevs.lunararc.common.bridge;

/** Paper's tab-list order API: ServerPlayer#getTabListOrder is a constant 0 in vanilla. */
public interface ServerPlayerListOrderBridge {
    int lunararc$getListOrder();
    void lunararc$setListOrder(int order);
}
