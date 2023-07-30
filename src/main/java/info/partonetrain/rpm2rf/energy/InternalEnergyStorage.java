package info.partonetrain.rpm2rf.energy;

import info.partonetrain.rpm2rf.RPM2RF;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class InternalEnergyStorage extends EnergyStorage{

    public InternalEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, 0);
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    public int internalConsumeEnergy(int consume) {
        int oenergy = energy;
        energy = Math.max(0, energy - consume);
        return oenergy - energy;
    }

    public int internalProduceEnergy(int produce) {
        int oenergy = energy;
        energy = Math.min(capacity, energy + produce);
        return oenergy - energy;
    }

    public void log() {
        RPM2RF.LOGGER.info(getEnergyStored() + "/" + getMaxEnergyStored() + "Max Extract: " + maxExtract + "Max Receive: " + maxReceive);
    }
}
