package io.lunararcdevs.lunararc.common.bridge.entity;

/** Paper daylight-burning state attached to the real NMS AbstractSkeleton. */
public interface AbstractSkeletonBridge extends BurnInDayBridge {
    void lunararc$setShouldBurnInDay(boolean burn);
}
