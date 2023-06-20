package Model.Types.Carriers;

import Model.Types.Package;

public class RegularCarrier extends Carrier {

    public RegularCarrier(Carrier carrier) {
        super(carrier);
    }

    public RegularCarrier (String name, double basePriceSmall, double basePriceMedium, double basePriceBig, double tax, double profitRate) {
        super(name, basePriceSmall, basePriceMedium, basePriceBig, tax, profitRate);
    }

    @Override
    public double calculateShippingCost (Package p) {
        double basePrice;


        int numberOfArticles = p.getNumberOfArticles();

        if (numberOfArticles <= 1) {
            basePrice = this.getBasePriceSmall();
        } else if (numberOfArticles <= 5) {
            basePrice = this.getBasePriceMedium();
        } else {
            basePrice = this.getBasePriceBig();
        }

        double profit = basePrice * this.getProfitRate();
        double tax = basePrice * this.getTax();
        basePrice = basePrice + profit + tax;

        return basePrice;
    }

    public void setProfit (Package p) {
        double basePrice;
        int numberOfArticles = p.getNumberOfArticles();

        if (numberOfArticles <= 1) {
            basePrice = this.getBasePriceSmall();
        } else if (numberOfArticles <= 5) {
            basePrice = this.getBasePriceMedium();
        } else {
            basePrice = this.getBasePriceBig();
        }

        this.addToMoneyEarned(basePrice * this.getProfitRate());
    }

    @Override
    public String carrierType() {
        return "regular";
    }

    @Override
    public Carrier clone() {
        return new RegularCarrier(this);
    }
}
