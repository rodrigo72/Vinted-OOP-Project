package Model.Types.Articles;

import java.io.Serializable;

public class RegularPremium implements PremiumBehaviour {
    private double markup;
    private int lastUpdated;

    public RegularPremium (double discountRate, int lastUpdated) {
        this.markup = discountRate;
        this.lastUpdated = lastUpdated;
    }
    public RegularPremium (PremiumBehaviour premium) {
        this.markup = premium.getMarkup();
        this.lastUpdated = premium.getLastUpdated();
    }
    @Override
    public double getPremiumPriceCorrection (double basePrice, int currentYear) {
        // assert currentYear >= collectionYear; error handling needed here
        double priceCorrection = basePrice * ((currentYear - this.lastUpdated) * this.markup);
        this.lastUpdated = currentYear;
        return priceCorrection;
    }

    @Override
    public double correctCollectionYearOffset(double basePrice, int offset) { return basePrice * offset * this.markup; }
    public int getLastUpdated() {
        return this.lastUpdated;
    }
    public void setLastUpdated(int lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public double getMarkup() { return this.markup; }
    public void setMarkup(double markup) { this.markup = markup; }

    @Override
    public RegularPremium clone() {
        return new RegularPremium(this);
    }

    @Override
    public String toString() {
        return "[premium] Markup percentage rate: " + this.markup;
    }
}

