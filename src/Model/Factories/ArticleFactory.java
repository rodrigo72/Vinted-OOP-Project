package Model.Factories;

import Model.Types.Articles.Article;
import Model.Types.Articles.Bag;
import Model.Types.Articles.Shoes;
import Model.Types.Articles.TShirt;
import Model.Types.Carriers.Carrier;

import java.util.UUID;

public class ArticleFactory {
    // used and premium
    public Article createBag (String description, String brand, double basePrice, int numberOfOwners, double stateOfUse,
                              double discountRate, Carrier carrier, UUID sellerID, int[] dimensions, String material,
                              int collectionYear, double discount) {
        return new Bag.BagBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour(numberOfOwners, stateOfUse)
                .setPremiumBehaviour(discountRate, collectionYear)
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setDimensions(dimensions)
                .setMaterial(material)
                .setCollectionYear(collectionYear)
                .setDiscount(discount)
                .buildWithoutPriceCorrection();
    }

    // used
    public Article createBag (String description, String brand, double basePrice, int numberOfOwners, double stateOfUse,
                              Carrier carrier, UUID sellerID, int[] dimensions, String material, int collectionYear, double discount) {
        return new Bag.BagBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour(numberOfOwners, stateOfUse)
                .setPremiumBehaviour()
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setDimensions(dimensions)
                .setMaterial(material)
                .setCollectionYear(collectionYear)
                .setDiscount(discount)
                .buildWithoutPriceCorrection();
    }

    // premium
    public Article createBag (String description, String brand, double basePrice, double discountRate, Carrier carrier,
                              UUID sellerID, int[] dimensions, String material, int collectionYear, double discount) {
        return new Bag.BagBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour()
                .setPremiumBehaviour(discountRate, collectionYear)
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setDimensions(dimensions)
                .setMaterial(material)
                .setCollectionYear(collectionYear)
                .setDiscount(discount)
                .buildWithoutPriceCorrection();
    }

    // none
    public Article createBag (String description, String brand, double basePrice, Carrier carrier, UUID sellerID,
                              int[] dimensions, String material, int collectionYear, double discount) {
        return new Bag.BagBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour()
                .setPremiumBehaviour()
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setDimensions(dimensions)
                .setMaterial(material)
                .setCollectionYear(collectionYear)
                .setDiscount(discount)
                .buildWithoutPriceCorrection();
    }

    // Used and Premium
    public Article createShoes(String description, String brand, double basePrice, int numberOfOwners, double stateOfUse,
                               double discountRate, Carrier carrier, UUID sellerID, int size, boolean hasLaces, String color,
                               int collectionYear, double discount) {
        return new Shoes.ShoesBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour(numberOfOwners, stateOfUse)
                .setPremiumBehaviour(discountRate, collectionYear)
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setSize(size)
                .setHasLaces(hasLaces)
                .setColor(color)
                .setCollectionYear(collectionYear)
                .setDiscount(discount)
                .buildWithoutPriceCorrection();
    }

    // Used
    public Article createShoes(String description, String brand, double basePrice, int numberOfOwners, double stateOfUse,
                               Carrier carrier, UUID sellerID, int size, boolean hasLaces, String color, int collectionYear, double discount) {
        return new Shoes.ShoesBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour(numberOfOwners, stateOfUse)
                .setPremiumBehaviour()
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setSize(size)
                .setHasLaces(hasLaces)
                .setColor(color)
                .setCollectionYear(collectionYear)
                .setDiscount(discount)
                .buildWithoutPriceCorrection();
    }

    // Premium
    public Article createShoes(String description, String brand, double basePrice, double discountRate, Carrier carrier,
                               UUID sellerID, int size, boolean hasLaces, String color, int collectionYear, double discount) {
        return new Shoes.ShoesBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour()
                .setPremiumBehaviour(discountRate, collectionYear)
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setSize(size)
                .setHasLaces(hasLaces)
                .setColor(color)
                .setCollectionYear(collectionYear)
                .setDiscount(discount)
                .buildWithoutPriceCorrection();
    }

    // None
    public Article createShoes(String description, String brand, double basePrice, Carrier carrier, UUID sellerID, int size,
                               boolean hasLaces, String color, int collectionYear, double discount) {
        return new Shoes.ShoesBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour()
                .setPremiumBehaviour()
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setSize(size)
                .setHasLaces(hasLaces)
                .setColor(color)
                .setCollectionYear(collectionYear)
                .setDiscount(discount)
                .buildWithoutPriceCorrection();
    }
    
    public Article createTShirt(String description, String brand, double basePrice, int numberOfOwners,
                                double stateOfUse, Carrier carrier, UUID sellerID, TShirt.Size size, TShirt.Pattern pattern) {
        return new TShirt.TShirtBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour(numberOfOwners, stateOfUse)
                .setPremiumBehaviour()
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setSize(size)
                .setPattern(pattern)
                .buildWithoutPriceCorrection();
    }

    public Article createTShirt(String description, String brand, double basePrice, Carrier carrier, UUID sellerID,
                                TShirt.Size size, TShirt.Pattern pattern) {
        return new TShirt.TShirtBuilder()
                .setDescription(description)
                .setBrand(brand)
                .setBasePrice(basePrice)
                .setUsedBehaviour()
                .setPremiumBehaviour()
                .setCarrier(carrier)
                .setSellerId(sellerID)
                .setSize(size)
                .setPattern(pattern)
                .buildWithoutPriceCorrection();
    }
}