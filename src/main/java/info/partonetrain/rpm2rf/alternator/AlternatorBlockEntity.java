package info.partonetrain.rpm2rf.alternator;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;

import info.partonetrain.rpm2rf.RPM2RF;
import info.partonetrain.rpm2rf.config.Config;
import info.partonetrain.rpm2rf.energy.InternalEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class AlternatorBlockEntity extends KineticBlockEntity{

    protected final InternalEnergyStorage energy;
    private LazyOptional<IEnergyStorage> lazyEnergy;

    //from CreateAddition's Util.java
    public static String format(int n) {
        if(n > 1000_000_000)
            return Math.round((double)n/100_000_000d)/10d + "G";
        if(n > 1000_000)
            return Math.round((double)n/100_000d)/10d + "M";
        if(n > 1000)
            return Math.round((double)n/100d)/10d + "K";
        return n + "";
    }

    public AlternatorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        energy = new InternalEnergyStorage(Config.ALTERNATOR_CAPACITY.get(), 0, Config.ALTERNATOR_MAX_OUTPUT.get());
        lazyEnergy = LazyOptional.of(() -> energy);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        //tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.stored").formatted(TextFormatting.GRAY)));
        //tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.getString(energy) + "fe").formatted(TextFormatting.AQUA)));
        tooltip.add(Component.literal(spacing).append(Component.translatable(RPM2RF.MODID + ".tooltip.energy.production").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(spacing).append(Component.literal(" " + this.format(getEnergyProductionRate((int) (isSpeedRequirementFulfilled() ? getSpeed() : 0))) + "fe/t ") // fix
                .withStyle(ChatFormatting.AQUA)).append(Lang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
        added = true;
        return added;
    }

    @Override
    public float calculateStressApplied() {
        float impact = Config.STRESS_MULTIPLIER.get(); //default config is 32
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(cap == ForgeCapabilities.ENERGY) // && (isEnergyInput(side) || isEnergyOutput(side)))
            return lazyEnergy.cast();
        return super.getCapability(cap, side);
    }

    public boolean isEnergyInput(Direction side) {
        return false;
    }

    public boolean isEnergyOutput(Direction side) {
        return true; //side != getBlockState().getValue(AlternatorBlock.FACING); //tried to get this to work, but it doesn't
    }

    @Override
    public void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        energy.read(compound);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        energy.write(compound);
    }

    private boolean firstTickState = true;

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide())
            return;
        if(firstTickState)
            firstTick();
        firstTickState = false;

        if(Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled())
            energy.internalProduceEnergy(getEnergyProductionRate((int)getSpeed()));


        for(Direction d : Direction.values()) {
            if(!isEnergyOutput(d))
                continue;
            IEnergyStorage ies = getCachedEnergy(d);
            if(ies == null)
                continue;
            int ext = energy.extractEnergy(ies.receiveEnergy(Config.ALTERNATOR_MAX_OUTPUT.get(), true), false);
        }
    }

    public static int getEnergyProductionRate(int rpm) {
        rpm = Math.abs(rpm);

        double multiplier = Config.ALTERNATOR_MULTIPLIER.get(); //1.0 by default
        int divisor = Config.ALTERNATOR_DIVISOR.get(); //8 by default
        double base_rate = (Math.pow(rpm, 2))/divisor; //this is where I learned Java doesn't have an exponent operator, and ^ is the xor operator.
        int output = (int)(base_rate * multiplier);

        //RPM2RF.LOGGER.info("RPM: " + rpm + ", base_rate: " + base_rate + ", divisor: " + divisor + ", multiplier: " + multiplier + ", Energy: " + output);
        return output;
    }

    @Override
    public void remove() {
        lazyEnergy.invalidate();
        super.remove();
    }

    public void firstTick() {
        updateCache();
    };

    public void updateCache() {
        if(level.isClientSide())
            return;
        for(Direction side : Direction.values()) {
            BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
            if(te == null) {
                setCache(side, LazyOptional.empty());
                continue;
            }
            LazyOptional<IEnergyStorage> le = te.getCapability(ForgeCapabilities.ENERGY, side.getOpposite());
            setCache(side, le);
        }
    }

    private LazyOptional<IEnergyStorage> escacheUp = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> escacheDown = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> escacheNorth = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> escacheEast = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> escacheSouth = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> escacheWest = LazyOptional.empty();

    public void setCache(Direction side, LazyOptional<IEnergyStorage> storage) {
        switch(side) {
            case DOWN:
                escacheDown = storage;
                break;
            case EAST:
                escacheEast = storage;
                break;
            case NORTH:
                escacheNorth = storage;
                break;
            case SOUTH:
                escacheSouth = storage;
                break;
            case UP:
                escacheUp = storage;
                break;
            case WEST:
                escacheWest = storage;
                break;
        }
    }

    public IEnergyStorage getCachedEnergy(Direction side) {
        switch(side) {
            case DOWN:
                return escacheDown.orElse(null);
            case EAST:
                return escacheEast.orElse(null);
            case NORTH:
                return escacheNorth.orElse(null);
            case SOUTH:
                return escacheSouth.orElse(null);
            case UP:
                return escacheUp.orElse(null);
            case WEST:
                return escacheWest.orElse(null);
        }
        return null;
    }
}