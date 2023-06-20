package Model.Types.Articles;

import java.io.Serializable;

public class RegularUsed implements UsedBehaviour {
    private int numberOfOwners;
    private double stateOfUse;

    public RegularUsed (int numberOfOwners, double stateOfUse) {
        this.numberOfOwners = numberOfOwners;
        this.stateOfUse = stateOfUse;
    }
    public RegularUsed (UsedBehaviour used) {
        this.numberOfOwners = used.getNumberOfOwners();
        this.stateOfUse = used.getStateOfUse();
    }
    @Override
    public double getUsedPriceCorrection(double basePrice) {
        return ((basePrice/this.numberOfOwners) * this.stateOfUse);
    }
    @Override
    public int getNumberOfOwners() {
        return this.numberOfOwners;
    }
    @Override
    public double getStateOfUse() {
        return this.stateOfUse;
    }
    public void setNumberOfOwners(int numberOfOwners) {
        this.numberOfOwners = numberOfOwners;
    }
    public void setStateOfUse(int stateOfUse) {
        this.stateOfUse = stateOfUse;
    }

    @Override
    public RegularUsed clone() {
        return new RegularUsed(this);
    }

    @Override
    public String toString() {
        return "[used] Number of owners: " + this.numberOfOwners + "; State of use: " + this.stateOfUse;
    }
}

