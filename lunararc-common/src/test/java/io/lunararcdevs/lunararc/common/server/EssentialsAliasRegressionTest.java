package io.lunararcdevs.lunararc.common.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.resources.Identifier;

public final class EssentialsAliasRegressionTest {
    public static void run() {
        Map<Identifier, String> materials = new ConcurrentHashMap<>();
        materials.put(Identifier.parse("example:gear"), "gear");
        AtomicInteger scans = new AtomicInteger();
        var index = new LunarArcEssentialsItemBridge.AliasIndex<>(() -> materials, item -> {
            scans.incrementAndGet();
            return true;
        });
        if (!"gear".equals(index.get("example_gear"))) throw new AssertionError("alias missing");
        for (int i = 0; i < 100000; i++) {
            if (index.get("missing_" + i) != null) throw new AssertionError("unknown alias resolved");
        }
        if (scans.get() != 1) throw new AssertionError("unknown aliases rescanned registry");
        materials.put(Identifier.parse("example:cog"), "cog");
        if (!"cog".equals(index.get("example_cog"))) throw new AssertionError("new registration not indexed");
        if (scans.get() != 3) throw new AssertionError("unexpected index rebuilds");
        materials.put(Identifier.parse("example:cogwheel"), "cogwheel");
        index.get("example_cog");
        if (!java.util.List.of("example_cog", "example_cogwheel").equals(index.withPrefix("example_co", 10))) throw new AssertionError("prefix search wrong");
        if (index.withPrefix("example_co", 1).size() != 1) throw new AssertionError("prefix limit ignored");
        if (!index.withPrefix("zzz", 10).isEmpty()) throw new AssertionError("prefix matched nothing expected");
        System.out.println("Essentials alias regressions passed");
    }
}
