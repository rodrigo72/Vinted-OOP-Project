package Model.Types.Articles;

import java.io.Serializable;

public interface UsedBehaviour extends Serializable {
    double getUsedPriceCorrection (double basePrice);
    int getNumberOfOwners();
    double getStateOfUse();
}

