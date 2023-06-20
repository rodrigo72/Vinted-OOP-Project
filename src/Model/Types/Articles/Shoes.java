package Model.Types.Articles;

import Model.Types.Carriers.Carrier;

import java.util.UUID;

public class Shoes extends Article {
    private int size;
    private boolean hasLaces;
    private String color;
    private int collectionYear;
    private double discount;

    public Shoes (Shoes shoes) {
        super(shoes);
        this.size = shoes.getSize();
        this.hasLaces = shoes.hasLaces();
        this.color = shoes.getColor();
        this.collectionYear = shoes.getCollectionYear();
        this.discount = shoes.getDiscount();
    }

    public Shoes (String description, String brand, double basePrice,
                  UsedBehaviour usedBehaviour, PremiumBehaviour premiumBehaviour, Carrier carrier, UUID sellerId,
                  int size, boolean hasLaces, String color, int collectionYear, double discount) {
        super(description, brand, basePrice, usedBehaviour, premiumBehaviour, carrier, sellerId);
        this.size = size;
        this.hasLaces = hasLaces;
        this.color = color;
        this.collectionYear = collectionYear;
        this.discount = discount;
        this.applyDiscount();
    }

    public Shoes (String description, String brand, double basePrice, double priceCorrection,
                  UsedBehaviour usedBehaviour, PremiumBehaviour premiumBehaviour, Carrier carrier, UUID sellerId,
                  int size, boolean hasLaces, String color, int collectionYear, double discount) {
        super(description, brand, basePrice, priceCorrection, usedBehaviour, premiumBehaviour, carrier, sellerId);
        this.size = size;
        this.hasLaces = hasLaces;
        this.color = color;
        this.collectionYear = collectionYear;
        this.discount = discount;
        this.applyDiscount();
    }

    public int getSize() {
        return this.size;
    }
    public void setSize(int size) {
        if (this.size < 45 && size > 45) {
            this.size = size;
            this.applyDiscount();
        } else if (this.size > 45 && size < 45){
            this.setPriceCorrection(this.getPriceCorrection() + calculateSizeDiscount());
            this.size = size;
        } else {
            this.size = size;
        }
    }

    public boolean hasLaces() {
        return this.hasLaces;
    }
    public void setHasLaces(boolean hasLaces) {
        this.hasLaces = hasLaces;
    }

    public String getColor() {
        return this.color;
    }
    public void setColor (String color) {
        this.color = color;
    }

    public int getCollectionYear() {
        return this.collectionYear;
    }
    public void setCollectionYear (int collectionYear) {
        this.correctPremiumPriceCorrection(this.collectionYear - collectionYear);
        this.collectionYear = collectionYear;
    }

    public double getDiscount() {
        return this.discount;
    }
    public void setDiscount(double discount) {
        this.setPriceCorrection(this.getPriceCorrection() + calculateSizeDiscount());
        this.discount = discount;
        this.applyDiscount();
    }
    private void applyDiscount() {
        if (this.size > 45) {
            this.setPriceCorrection(this.getPriceCorrection() - this.calculateSizeDiscount());
        }
    }

    private double calculateSizeDiscount() {
        return this.getBasePrice() * this.discount;
    }

    private double calculateCollectionDiscount(int currentYear) {
        return this.getPremiumBehaviour().getPremiumPriceCorrection(this.getBasePrice(), currentYear);
    }

    private double calculateUsedDiscount() {
        return this.getUsedBehaviour().getUsedPriceCorrection(this.getBasePrice());
    }

    @Override
    public void updatePriceCorrection(int currentYear) {
        this.updateUsedPriceCorrection();
        this.updatePremiumPriceCorrection(currentYear);
    }

    public void updateUsedPriceCorrection () {
        this.setPriceCorrection(this.getPriceCorrection() - calculateUsedDiscount());
    }

    public void updatePremiumPriceCorrection(int currentYear) {
        this.setPriceCorrection(this.getPriceCorrection() + this.calculateCollectionDiscount(currentYear));
    }

    public void correctPremiumPriceCorrection(int offset) {
        this.setPriceCorrection(this.getPriceCorrection() + this.getPremiumBehaviour().correctCollectionYearOffset(this.getBasePrice(), offset));
    }

    @Override
    public String toString() {
        return super.toString() +
                "Size = " + size + "\n" +
                "Laces = " + hasLaces + "\n" +
                "Color = '" + color + '\'' + "\n" +
                "Collection year = " + collectionYear + "\n";
    }

    @Override
    public String articleType() {
        return "shoes";
    }

    @Override
    public Shoes clone() {
        return new Shoes(this);
    }

    protected static abstract class GenericShoesBuilder<B extends GenericShoesBuilder<B>> extends Article.ArticleBuilder<B> {
        private boolean prerequisiteMet = false;
        protected int size;
        protected boolean hasLaces;
        protected String color;
        protected int collectionYear;
        protected double discount;

        public B setPremiumBehaviour (double discount) {
            if (!this.prerequisiteMet) {
                throw new IllegalStateException("set premium behaviour with only discount as argument, needs to have a collection year set");
            }
            this.premiumBehaviour = new RegularPremium(discount, this.collectionYear);
            return self();
        }

        public B setSize(int size) {
            this.size = size;
            return self();
        }

        public B setHasLaces(boolean hasLaces) {
            this.hasLaces = hasLaces;
            return self();
        }

        public B setColor(String color) {
            this.color = color;
            return self();
        }

        public B setCollectionYear(int collectionYear) {
            this.prerequisiteMet = true;
            this.collectionYear = collectionYear;
            return self();
        }

        public B setDiscount(double discount) {
            this.discount = discount;
            return self();
        }

    }

    public static class ShoesBuilder extends GenericShoesBuilder<ShoesBuilder> {
        @Override
        public Shoes buildWithoutPriceCorrection() {
            return new Shoes(
                    this.description,
                    this.brand,
                    this.basePrice,
                    this.usedBehaviour,
                    this.premiumBehaviour,
                    this.carrier,
                    this.sellerId,
                    this.size,
                    this.hasLaces,
                    this.color,
                    this.collectionYear,
                    this.discount);
        }

        @Override
        public Shoes buildWithPriceCorrection() {
            return new Shoes(
                    this.description,
                    this.brand,
                    this.basePrice,
                    this.priceCorrection,
                    this.usedBehaviour,
                    this.premiumBehaviour,
                    this.carrier,
                    this.sellerId,
                    this.size,
                    this.hasLaces,
                    this.color,
                    this.collectionYear,
                    this.discount);
        }
    }
}
