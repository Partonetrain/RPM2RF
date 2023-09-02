package info.partonetrain.rpm2rf.registry;

import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import info.partonetrain.rpm2rf.RPM2RF;
import info.partonetrain.rpm2rf.alternator.AlternatorBlock;
import info.partonetrain.rpm2rf.config.Config;
import net.minecraft.client.renderer.RenderType;

import static com.simibubi.create.AllCreativeModeTabs.BASE_CREATIVE_TAB;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModBlocks {

    static {
        RPM2RF.REGISTRATE.creativeModeTab(() -> BASE_CREATIVE_TAB);
        //this is Create's creative tab, although it won't show up there, this is just to add it to creative search
    }

    public static final BlockEntry<AlternatorBlock> ALTERNATOR = RPM2RF.REGISTRATE.block("alternator", AlternatorBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(BlockStressDefaults.setImpact(Config.STRESS_MULTIPLIER.get()))
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {

    }
}
