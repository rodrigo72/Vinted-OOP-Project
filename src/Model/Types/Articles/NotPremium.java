package Model.Types.Articles;

import java.io.Serializable;

public class NotPremium implements PremiumBehaviour {
    @Override
    public double getPremiumPriceCorrection(double basePrice, int currentYear) {
        return 0;
    }
    public double correctCollectionYearOffset(double basePrice, int offset) {return 0;}

    public double getMarkup() {
        return 0;
    }
    @Override
    public int getLastUpdated() { return 0; }

    public void setMarkup(double markup) {
        // do nothing
    }

    @Override
    public String toString() {
        return "[not premium]";
    }
}
