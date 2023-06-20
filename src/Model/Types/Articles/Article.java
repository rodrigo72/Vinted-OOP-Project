package Model.Types.Articles;

import Logs.AppLogger;
import Model.Types.Carriers.Carrier;
import Model.Types.HasId;

import java.io.Serializable;
import java.util.UUID;

public abstract class Article implements Comparable<Article>, Cloneable, HasId, Serializable {
    private UUID id;
    private String description;
    private String brand;
    private double basePrice;
    private double priceCorrection;
    private UsedBehaviour usedBehaviour;
    private PremiumBehaviour premiumBehaviour;
    private Carrier carrier;
    private UUID sellerId;

    public Article (Article article) {
        this.id = article.getId();
        this.description = article.getDescription();
        this.brand = article.getBrand();
        this.basePrice = article.getBasePrice();
        this.priceCorrection = article.getPriceCorrection();
        this.usedBehaviour = article.getUsedBehaviour();
        this.premiumBehaviour = article.getPremiumBehaviour();
        this.carrier = article.getCarrier();
        this.sellerId = article.getSellerId();
    }

    public Article (String description, String brand, double basePrice, double priceCorrection,
                    UsedBehaviour usedBehaviour, PremiumBehaviour premiumBehaviour, Carrier carrier, UUID sellerId) {
        this.description = description;
        this.brand = brand;
        this.basePrice = basePrice;
        this.priceCorrection = priceCorrection;
        this.usedBehaviour = usedBehaviour;
        this.premiumBehaviour = premiumBehaviour;
        this.carrier = carrier;
        this.sellerId = sellerId;
    }

    public Article(String description, String brand, double basePrice,
                   UsedBehaviour usedBehaviour, PremiumBehaviour premiumBehaviour, Carrier carrier, UUID sellerId) {
        this.description = description;
        this.brand = brand;
        this.basePrice = basePrice;
        this.priceCorrection = basePrice;
        this.usedBehaviour = usedBehaviour;
        this.premiumBehaviour = premiumBehaviour;
        this.carrier = carrier;
        this.sellerId = sellerId;
    }

    public String getDescription() {
        return this.description;
    }
    public String getBrand() {
        return this.brand;
    }

    public UUID getId() {
        return this.id;
    }
    public double getBasePrice() {
        return this.basePrice;
    }
    public double getPriceCorrection() {
        return this.priceCorrection;
    }
    public UsedBehaviour getUsedBehaviour() {
        return this.usedBehaviour;
    }
    public PremiumBehaviour getPremiumBehaviour() {
        return this.premiumBehaviour;
    }
    public Carrier getCarrier() {
        return this.carrier;
    }
    public UUID getSellerId() {
        return this.sellerId;
    }
    public UUID getCarrierID() {
        return this.carrier.getId();
    }

    public void setDescription (String description) { this.description = description; }
    public void setBrand (String brand) { this.brand = brand; }
    public void setId (UUID id) {this.id = id; }
    public void setBasePrice (double basePrice) { this.basePrice = basePrice; }
    public void setPriceCorrection (double priceCorrection) { this.priceCorrection = priceCorrection; }
    public void setPremiumBehaviour(PremiumBehaviour premiumBehaviour) {
        this.premiumBehaviour = premiumBehaviour;
    }
    public void setUsedBehaviour(UsedBehaviour usedBehaviour) {
        this.usedBehaviour = usedBehaviour;
    }
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }
    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        String articleType = articleType();
        return articleType +  "\n" +
                // "ID = '" + id + "'\n" +
                // "SellerID = '" + sellerId + "'\n" +
                "Description = '" + description + "'\n" +
                "Brand = '" + brand + "'\n" +
                "Price = " + priceCorrection + "\n" +
                "Used behaviour = " + usedBehaviour + "\n" +
                "Premium = " + premiumBehaviour + "\n";
                // "Carrier = " + carrier + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || (obj.getClass() != this.getClass())) return false;
        Article a = (Article) obj;
        return this.id.equals(a.getId());
    }

    public boolean isUsed() {
        return !(this.getUsedBehaviour() instanceof NotUsed);
    }

    public boolean isPremium() {
        return !(this.getPremiumBehaviour() instanceof NotPremium);
    }

    protected abstract String articleType();
    public abstract void updatePriceCorrection(int currentYear);
    public abstract void updatePremiumPriceCorrection (int currentYear);

    public int compareTo(Article o) {
        return Double.compare(this.basePrice, o.getBasePrice());
    }

    @Override
    public Article clone() {
        try {
            return (Article) super.clone();
        } catch (CloneNotSupportedException e) {
            AppLogger.logError(e);
            throw new AssertionError();
        }
    }

    protected static abstract class ArticleBuilder<B extends ArticleBuilder<B>> {
        protected String description;
        protected String brand;
        protected UUID id;
        protected double basePrice;
        protected double priceCorrection;
        protected UsedBehaviour usedBehaviour;
        protected PremiumBehaviour premiumBehaviour;
        protected Carrier carrier;
        protected UUID sellerId;

        public B setDescription(String description) {
            this.description = description;
            return self();
        }

        public B setBrand(String brand) {
            this.brand = brand;
            return self();
        }

        public B setId (UUID id) {
            this.id = id;
            return self();
        }

        public B setBasePrice(double basePrice) {
            this.basePrice = basePrice;
            this.priceCorrection = basePrice;
            return self();
        }

        public B setPriceCorrection (double priceCorrection) {
            this.priceCorrection = priceCorrection;
            return self();
        }

        public B setUsedBehaviour(int numberOfOwners, double stateOfUse) {
            this.usedBehaviour = new RegularUsed(numberOfOwners, stateOfUse);
            return self();
        }

        public B setUsedBehaviour() {
            this.usedBehaviour = new NotUsed();
            return self();
        }

        public B setUsedBehaviour (UsedBehaviour ub) {
            this.usedBehaviour = new RegularUsed(ub);
            return self();
        }

        public B setPremiumBehaviour(double discountRate, int lastUpdated) {
            this.premiumBehaviour = new RegularPremium(discountRate, lastUpdated);
            return self();
        }

        public B setPremiumBehaviour() {
            this.premiumBehaviour = new NotPremium();
            return self();
        }

        public B setPremiumBehaviour (PremiumBehaviour pb) {
            this.premiumBehaviour = new RegularPremium(pb);
            return self();
        }

        public B setCarrier(Carrier carrier) {
            this.carrier = carrier;
            return self();
        }

        public B setSellerId (UUID sellerId) {
            this.sellerId = sellerId;
            return self();
        }

        protected abstract Article buildWithoutPriceCorrection();
        protected abstract Article buildWithPriceCorrection();

        @SuppressWarnings("unchecked")
        protected final B self() {
            return (B) this;
        }
    }

}
