package info.partonetrain.rpm2rf.energy;

import net.minecraftforge.energy.EnergyStorage;

public class ModEnergyStorage extends EnergyStorage {

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public int getMaxExtract() {
        return maxExtract; //this is protected to we need a method
    }

    public void generateEnergy(int energy) {
        this.energy += energy;
        if(this.energy > capacity){
            this.energy = capacity;
        }
    }

    public void consumeEnergy(int energy) {
        this.energy -= energy;
        if(this.energy < 0) {
            this.energy = 0;
        }
    }
}
