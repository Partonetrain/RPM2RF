package info.partonetrain.rpm2rf.alternator;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;

import info.partonetrain.rpm2rf.RPM2RF;
import info.partonetrain.rpm2rf.config.Config;
import info.partonetrain.rpm2rf.energy.ModEnergyStorage;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AlternatorBlockEntity extends KineticBlockEntity{

    protected final ModEnergyStorage energy;

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
        energy = new ModEnergyStorage(Config.ALTERNATOR_CAPACITY.get(), 0, Config.ALTERNATOR_MAX_OUTPUT.get());
        lazyEnergy = LazyOptional.of(() -> energy);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        tooltip.add(Component.literal(spacing).append(Component.translatable(RPM2RF.MODID + ".tooltip.energy.production").withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal(spacing).append(Component.literal(" " + this.format(getEnergyProductionRate((int) (isSpeedRequirementFulfilled() ? getSpeed() : 0))) + "fe/t ") // fix
                .withStyle(ChatFormatting.AQUA)).append(Lang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
        return true;
    }

    @Override
    public float calculateStressApplied() {
        float impact = Config.STRESS_MULTIPLIER.get(); //default config is 32
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(cap == ForgeCapabilities.ENERGY)
            return lazyEnergy.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
    }

    @Override
    public void tick() {

        super.tick();

        //generate and store the appropriate amount of energy
        if(Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled()){
            energy.generateEnergy(getEnergyProductionRate((int)Math.abs(getSpeed())));
            //RPM2RF.LOGGER.info("Generated: " + getEnergyProductionRate((int)Math.abs(getSpeed())) );
        }

        //transfer energy to adjacent energy-capable blocks
        for(int i = 0; (i < Direction.values().length) && (energy.getMaxEnergyStored() > 0); i++) {
            Direction facing = Direction.values()[i];

            BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(facing));
            if (blockEntity == null)
                continue;
            blockEntity.getCapability(ForgeCapabilities.ENERGY, facing.getOpposite()).ifPresent(ies -> { //ies = IEnergyStorage of other BlockEntity
                        if(ies.canReceive()) {
                            int received = ies.receiveEnergy(Math.min(energy.getEnergyStored(), energy.getMaxExtract()), false);
                            energy.consumeEnergy(received);
                            setChanged();
                            //RPM2RF.LOGGER.info("Energy output: " + received);
                        }
                    });
        }

        //RPM2RF.LOGGER.info("ticker:" + ticker);
    }

    public static int getEnergyProductionRate(int rpm) {
        rpm = Math.abs(rpm);

        double multiplier = Config.ALTERNATOR_MULTIPLIER.get(); //1.0 by default
        int divisor = Config.ALTERNATOR_DIVISOR.get(); //8 by default
        double base_rate = (Math.pow(rpm, 2))/divisor; //this is where I learned Java doesn't have an exponent operator, and ^ is the xor operator.
        int output = (int)(base_rate * multiplier);

        //RPM2RF.LOGGER.info("ENERGY CALC: " + output + " at " + rpm + "rpms");
        return output;
    }

    @Override
    public void remove() {
        lazyEnergy.invalidate();
        super.remove();
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
        return switch (side) {
            case DOWN -> escacheDown.orElse(null);
            case EAST -> escacheEast.orElse(null);
            case NORTH -> escacheNorth.orElse(null);
            case SOUTH -> escacheSouth.orElse(null);
            case UP -> escacheUp.orElse(null);
            case WEST -> escacheWest.orElse(null);
        };
    }
}
