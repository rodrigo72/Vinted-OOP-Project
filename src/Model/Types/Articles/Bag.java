package Model.Types.Articles;

import Model.Types.Carriers.Carrier;
import Model.Types.Exceptions.ArticleNegativeNumberException;

import java.util.UUID;

public class Bag extends Article {

    private int[] dimensions;
    private String material;
    private int collectionYear;
    private double discount;

    public Bag (Bag bag) {
        super(bag);
        this.dimensions = bag.getDimensions();
        this.material = bag.getMaterial();
        this.collectionYear = bag.collectionYear;
        this.discount = bag.getDiscount();
    }

    public Bag (String description, String brand, double basePrice,
                UsedBehaviour usedBehaviour, PremiumBehaviour premiumBehaviour, Carrier carrier, UUID sellerId,
                int[] dimensions, String material, int collectionYear, double discount) {
        super(description, brand, basePrice, usedBehaviour, premiumBehaviour, carrier, sellerId);
        this.dimensions = dimensions;
        this.material = material;
        this.collectionYear = collectionYear;
        this.discount = discount;
        this.applyDiscount();
    }

    public Bag (String description, String brand, double basePrice, double priceCorrection,
                UsedBehaviour usedBehaviour, PremiumBehaviour premiumBehaviour, Carrier carrier, UUID sellerId,
                int[] dimensions, String material, int collectionYear, double discount) {
        super(description, brand, basePrice, priceCorrection, usedBehaviour, premiumBehaviour, carrier, sellerId);
        this.dimensions = dimensions;
        this.material = material;
        this.collectionYear = collectionYear;
        this.discount = discount;
        this.applyDiscount();
    }

    public int[] getDimensions() {
        return this.dimensions;
    }
    public String getMaterial() {
        return this.material;
    }
    public int getCollectionYear() {
        return this.collectionYear;
    }
    public void setDimensions (int[] dimensions) {
        this.setPriceCorrection(this.getPriceCorrection() + this.calculateVolumeDiscount());
        this.dimensions = dimensions;
        this.applyDiscount();
    }

    public void setDimension (int index, int value) {
        if (index < 0 || index >= 3) {
            throw new IndexOutOfBoundsException("Index out of bounds in setDimension");
        }
        this.setPriceCorrection(this.getPriceCorrection() + this.calculateVolumeDiscount());
        this.dimensions[index] = value;
        this.applyDiscount();
    }

    public void setMaterial (String material) {
        this.material = material;
    }
    public void setDiscount (double discount) {
        this.discount = discount;
    }

    public void setCollectionYear (int collectionYear) throws ArticleNegativeNumberException {
        if (collectionYear < 0) {
            throw new ArticleNegativeNumberException("Negative collection year in setCollectionYear");
        }
        this.correctPremiumPriceCorrection(this.collectionYear - collectionYear);
        this.collectionYear = collectionYear;
    }
    public double getDiscount() {
        return this.discount;
    }

    private void applyDiscount() {
        this.setPriceCorrection(this.getPriceCorrection() - this.calculateVolumeDiscount());
    }

    private double calculateVolumeDiscount() {
        int volume = 1;
        for (int dim : this.dimensions) {
            volume *= dim;
        }

        return (volume * this.discount * this.getBasePrice())/100.0;
    }

    public void correctPremiumPriceCorrection(int offset) {
        this.setPriceCorrection(this.getPriceCorrection() + this.getPremiumBehaviour().correctCollectionYearOffset(this.getBasePrice(), offset));
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

    @Override
    public String articleType() {
        return "bag";
    }

    @Override
    public String toString() {
        return super.toString() +
                "Dimensions = " + dimensions[0] + "x" + dimensions[1] + "x" + dimensions[2] + "\n" +
                "Material = '" + material + '\'' + "\n" +
                "Collection year = " + collectionYear + "\n";
    }

    @Override
    public Bag clone() {
        return new Bag(this);
    }

    protected static abstract class GenericBagBuilder<B extends GenericBagBuilder<B>> extends Article.ArticleBuilder<B> {
        private boolean prerequisiteMet = false;
        protected int[] dimensions;
        protected String material;
        protected int collectionYear;
        protected double discount;

        public B setPremiumBehaviour (double discount) {
            if (!this.prerequisiteMet) {
                throw new IllegalStateException("set premium behaviour with only discount argument, needs to have a collection year set");
            }
            this.premiumBehaviour = new RegularPremium(discount, this.collectionYear);
            return self();
        }

        public B setDimensions(int[] dimensions) {
            this.dimensions = dimensions;
            return self();
        }

        public B setMaterial(String material) {
            this.material = material;
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

    public static class BagBuilder extends GenericBagBuilder<BagBuilder> {
        @Override
        public Bag buildWithoutPriceCorrection() {
            return new Bag(
                    this.description,
                    this.brand,
                    this.basePrice,
                    this.usedBehaviour,
                    this.premiumBehaviour,
                    this.carrier,
                    this.sellerId,
                    this.dimensions,
                    this.material,
                    this.collectionYear,
                    this.discount);
        }

        @Override
        public Bag buildWithPriceCorrection() {
            return new Bag(
                    this.description,
                    this.brand,
                    this.basePrice,
                    this.priceCorrection,
                    this.usedBehaviour,
                    this.premiumBehaviour,
                    this.carrier,
                    this.sellerId,
                    this.dimensions,
                    this.material,
                    this.collectionYear,
                    this.discount);
        }
    }
}

