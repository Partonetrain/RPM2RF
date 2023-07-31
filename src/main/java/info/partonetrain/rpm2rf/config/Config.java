package info.partonetrain.rpm2rf.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Config {

    public static ForgeConfigSpec COMMON_CONFIG;
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    //general
    //public static ForgeConfigSpec.IntValue FE_RPM;
    //alternator
    public static ForgeConfigSpec.IntValue STRESS_MULTIPLIER;
    public static ForgeConfigSpec.IntValue ALTERNATOR_MAX_OUTPUT;
    public static ForgeConfigSpec.IntValue ALTERNATOR_CAPACITY;
    public static ForgeConfigSpec.IntValue ALTERNATOR_DIVISOR;
    public static ForgeConfigSpec.DoubleValue ALTERNATOR_MULTIPLIER;


    static {
        COMMON_BUILDER.comment("These are not synced over the network, so make sure client/server have same config values.");

        COMMON_BUILDER.comment("Alternator").push("alternator");
        STRESS_MULTIPLIER = COMMON_BUILDER.comment("Stress Units generated per RPM")
                .defineInRange("stress_multiplier", 32, 0, Integer.MAX_VALUE);
        ALTERNATOR_MAX_OUTPUT = COMMON_BUILDER.comment("Alternator max energy output in FE (Energy transfer, not generation).").comment("Note: ~4096 FE/t is the highest transfer rate in multiple mods.")
                .defineInRange("generator_max_output", 8192, 1, Integer.MAX_VALUE);
        ALTERNATOR_CAPACITY = COMMON_BUILDER.comment("Alternator internal capacity in FE.")
                .defineInRange("generator_capacity", 8192, 1, Integer.MAX_VALUE);
        ALTERNATOR_DIVISOR = COMMON_BUILDER.comment("Alternator divisor. Since the FE/t formula is (fe=(rpm^2)/divisor), lower numbers make power ramp up per RPM faster.")
                .defineInRange("generator_divisor", 8, 1, Integer.MAX_VALUE);
        ALTERNATOR_MULTIPLIER = COMMON_BUILDER.comment("Alternator multiplier. This is applied after all other power calculations. Can be useful to quickly nerf the alternator.")
                .defineInRange("generator_multiplier", 1.0d, 0.01d, 1.0d);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, java.nio.file.Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }

}