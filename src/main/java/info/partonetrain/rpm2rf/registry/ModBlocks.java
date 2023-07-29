package info.partonetrain.rpm2rf.registry;

import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import info.partonetrain.rpm2rf.RPM2RF;
import info.partonetrain.rpm2rf.alternator.AlternatorBlock;
import info.partonetrain.rpm2rf.config.Config;

import static com.simibubi.create.AllCreativeModeTabs.BASE_CREATIVE_TAB;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModBlocks {

    static {
        RPM2RF.REGISTRATE.creativeModeTab(() -> BASE_CREATIVE_TAB); //this is Create's creative tab
    }

    public static final BlockEntry<AlternatorBlock> ALTERNATOR = RPM2RF.REGISTRATE.block("alternator", AlternatorBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(BlockStressDefaults.setImpact(Config.STRESS_MULTIPLIER.get()))
            .tag(AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {

    }
}
