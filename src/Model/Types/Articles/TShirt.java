package Model.Types.Articles;

import Model.Types.Carriers.Carrier;

import java.util.UUID;

public class TShirt extends Article {
    public enum Size {
        S, M, L, XL
    }

    public enum Pattern {
        SOLID, STRIPES, PALM_TREES
    }

    private Size size;
    private Pattern pattern;
    private static final double FIXED_DISCOUNT = 0.5;

    public TShirt (TShirt tShirt) {
        super(tShirt);
        this.size = tShirt.getSize();
        this.pattern = tShirt.getPattern();
    }

    public TShirt (String description, String brand, double basePrice,
                   UsedBehaviour usedBehaviour, PremiumBehaviour premiumBehaviour, Carrier carrier, UUID sellerId,
                   Size size, Pattern pattern) {
        super(description, brand, basePrice, usedBehaviour, premiumBehaviour, carrier, sellerId);
        this.size = size;
        this.pattern = pattern;
        this.applyFixedDiscount();
    }

    public TShirt (String description, String brand, double basePrice, double priceCorrection,
                   UsedBehaviour usedBehaviour, PremiumBehaviour premiumBehaviour, Carrier carrier, UUID sellerId,
                   Size size, Pattern pattern) {
        super(description, brand, basePrice, priceCorrection, usedBehaviour, premiumBehaviour, carrier, sellerId);
        this.size = size;
        this.pattern = pattern;
        this.applyFixedDiscount();
    }

    public Size getSize() {
        return this.size;
    }

    public void setSize (Size size) {
        this.size = size;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public void setPattern (Pattern pattern) {
        this.setPriceCorrection(this.getPriceCorrection() + calculateDiscount());
        this.pattern = pattern;
        this.applyFixedDiscount();
    }

    public double getFixedDiscount() {
        return FIXED_DISCOUNT;
    }

    private double calculateDiscount() {
        return this.getBasePrice() * FIXED_DISCOUNT;
    }

    private void applyFixedDiscount() {
        if (this.pattern != Pattern.SOLID && this.isUsed()) {
            this.setPriceCorrection(this.getPriceCorrection() - calculateDiscount());
        }
    }

    @Override
    public String articleType() {
        return "t-shirt";
    }
    @Override
    public void updatePriceCorrection(int currentYear) {
        this.setPriceCorrection(this.getPriceCorrection() - this.getUsedBehaviour().getUsedPriceCorrection(this.getBasePrice()));
    }

    @Override
    public void updatePremiumPriceCorrection(int currentYear) {
        // do nothing
    }

    @Override
    public String toString() {
        return super.toString() +
                "Size = " + size + "\n" +
                "Pattern = " + pattern + "\n";
    }

    @Override
    public TShirt clone() {
        return new TShirt(this);
    }

    protected static abstract class GenericTShirtBuilder<B extends GenericTShirtBuilder<B>> extends Article.ArticleBuilder<B> {
        public Size size;
        public Pattern pattern;

        public B setSize (Size size) {
            this.size = size;
            return self();
        }

        public B setPattern (Pattern pattern) {
            this.pattern = pattern;
            return self();
        }
    }

    public static class TShirtBuilder extends GenericTShirtBuilder<TShirtBuilder> {
        @Override
        public TShirt buildWithoutPriceCorrection() {
            return new TShirt(
                    this.description,
                    this.brand,
                    this.basePrice,
                    this.usedBehaviour,
                    this.premiumBehaviour,
                    this.carrier,
                    this.sellerId,
                    this.size,
                    this.pattern);
        }

        @Override
        public TShirt buildWithPriceCorrection() {
            return new TShirt(
                    this.description,
                    this.brand,
                    this.basePrice,
                    this.priceCorrection,
                    this.usedBehaviour,
                    this.premiumBehaviour,
                    this.carrier,
                    this.sellerId,
                    this.size,
                    this.pattern);
        }
    }
}

