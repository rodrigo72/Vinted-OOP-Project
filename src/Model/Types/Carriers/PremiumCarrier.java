package Model.Types.Carriers;

import Model.Types.Articles.Article;
import Model.Types.Package;

public class PremiumCarrier extends Carrier {

    private double additionalPriceForPremiumArticles;
    public PremiumCarrier (Carrier carrier) {
        super(carrier);
        this.additionalPriceForPremiumArticles = 0;
    }

    public PremiumCarrier (PremiumCarrier carrier) {
        super(carrier);
        this.additionalPriceForPremiumArticles = carrier.getAdditionalPriceForPremiumArticles();
    }

    public PremiumCarrier(String name, double basePriceSmall, double basePriceMedium, double basePriceBig, double tax, double profitRate,
                          double additionalPriceForPremiumArticles) {
        super(name, basePriceSmall, basePriceMedium, basePriceBig, tax, profitRate);
        this.additionalPriceForPremiumArticles = additionalPriceForPremiumArticles;
    }

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

        basePrice *= (1 + this.getTax() + this.getProfitRate());

        for (Article article : p.getArticles()) {
            if (article.isPremium()) {
                basePrice += this.additionalPriceForPremiumArticles;
            }
        }

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

        basePrice = basePrice * this.getProfitRate();
        for (Article article : p.getArticles()) {
            if (article.isPremium()) {
                basePrice += this.additionalPriceForPremiumArticles;
            }
        }

        this.addToMoneyEarned(basePrice);
    }

    @Override
    public String carrierType() {
        return "premium";
    }

    public double getAdditionalPriceForPremiumArticles() {
        return additionalPriceForPremiumArticles;
    }

    public void setAdditionalPriceForPremiumArticles(double additionalPriceForPremiumArticles) {
        this.additionalPriceForPremiumArticles = additionalPriceForPremiumArticles;
    }

    @Override
    public Carrier clone() {
        return new PremiumCarrier(this);
    }
}
