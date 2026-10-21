package io.lunararcdevs.lunararc.common.bridge.access;

import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** Normal runtime bridge implemented by the corresponding Mixin accessor/invoker. */
public interface StructureTemplateManagerAccessBridge {
    Map<Identifier, Optional<StructureTemplate>> lunararc$getStructureRepository();
}
