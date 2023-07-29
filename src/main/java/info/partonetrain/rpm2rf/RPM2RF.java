package info.partonetrain.rpm2rf;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import info.partonetrain.rpm2rf.network.EnergyNetworkPacket;
import info.partonetrain.rpm2rf.network.ObservePacket;
import info.partonetrain.rpm2rf.ponder.PonderScenes;
import info.partonetrain.rpm2rf.registry.ModBlockEntities;
import info.partonetrain.rpm2rf.registry.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import com.simibubi.create.foundation.data.CreateRegistrate;

import info.partonetrain.rpm2rf.config.Config;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RPM2RF.MODID)
public class RPM2RF
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "rpm2rf";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(RPM2RF.MODID);

    private static final String PROTOCOL = "1";
    public static final SimpleChannel Network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

    public RPM2RF()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::ClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::PostInit);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("rpm2rf-common.toml"));

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModBlocks.register();
        ModBlockEntities.register();


        REGISTRATE.registerEventListeners(modEventBus);
    }


    private void ClientSetup(final FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            HELPER.addStoryBoard(ModBlocks.ALTERNATOR, "alternator", PonderScenes::alternator, AllPonderTags.KINETIC_APPLIANCES);
        });
    }

    public void PostInit(FMLLoadCompleteEvent evt) {
        int i = 0;
        //Network.registerMessage(i++, ObservePacket.class, ObservePacket::encode, ObservePacket::decode, ObservePacket::handle);
        //Network.registerMessage(i++, EnergyNetworkPacket.class, EnergyNetworkPacket::encode, EnergyNetworkPacket::decode, EnergyNetworkPacket::handle);

        LOGGER.debug("RPM2RF Initialized");
    }
}
