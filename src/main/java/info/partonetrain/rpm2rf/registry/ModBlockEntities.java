package info.partonetrain.rpm2rf.registry;

import info.partonetrain.rpm2rf.RPM2RF;
import info.partonetrain.rpm2rf.alternator.AlternatorBlockEntity;
import info.partonetrain.rpm2rf.alternator.AlternatorRenderer;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntities {

    public static final BlockEntityEntry<AlternatorBlockEntity> ALTERNATOR = RPM2RF.REGISTRATE
            .blockEntity("alternator", AlternatorBlockEntity::new)
            .validBlocks(ModBlocks.ALTERNATOR)
            .renderer(() -> AlternatorRenderer::new)
            .register();

    public static void register() {}
}
