package Model.Types.Articles;

import java.io.Serializable;

public class NotUsed implements UsedBehaviour {
    @Override
    public double getUsedPriceCorrection(double basePrice) {
        return 0;
    }
    @Override
    public int getNumberOfOwners() {
        return 0;
    }
    @Override
    public double getStateOfUse() {
        return 0;
    }

    @Override
    public String toString() {
        return "[not used]";
    }
}

