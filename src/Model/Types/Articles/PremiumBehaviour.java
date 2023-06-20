package Model.Types.Articles;

import java.io.Serializable;

public interface PremiumBehaviour extends Serializable {
    double getPremiumPriceCorrection (double basePrice, int currentYear);
    double correctCollectionYearOffset(double basePrice, int offset);
    double getMarkup();
    int getLastUpdated();
}
