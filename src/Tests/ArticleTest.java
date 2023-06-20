import Model.Data.Catalogs.ArticleCatalog;
import Model.Data.Catalogs.CarrierCatalog;
import Model.Types.Articles.*;
import Model.Types.Carriers.Carrier;
import Model.Types.Exceptions.ArticleNegativeNumberException;
import Model.Types.Carriers.RegularCarrier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {
    private double delta = 0.00001;
    Carrier carrier = new RegularCarrier("CTT", 1, 2, 3, 0.23, 0.05);
    CarrierCatalog carrierCatalog = new CarrierCatalog();

    int[] dimensions = {1, 2, 3};
    UUID uuid = UUID.randomUUID();

    @Test
    void testAddCarrierToCatalog() {
        carrierCatalog.add(carrier);
        UUID id = carrier.getId();
        carrierCatalog.get(id);
        assertEquals(carrier, carrierCatalog.get(id));
    }

    @Test
    void testBag() throws ArticleNegativeNumberException {
        Bag bag = new Bag
                .BagBuilder()
                .setDescription("Nice bag")
                .setBrand("Gucci")
                .setBasePrice(2099)
                .setUsedBehaviour(5, 0.2)
                .setPremiumBehaviour(0.3, 2003)
                .setCarrier(carrier)
                .setId(uuid)
                .setDimensions(dimensions)
                .setMaterial("neon leather")
                .setCollectionYear(2003)
                .setDiscount(0.15)
                .buildWithoutPriceCorrection();

        bag.updatePriceCorrection(2010);

        assertEquals(bag.getDescription(), "Nice bag");
        assertEquals(bag.getBrand(), "Gucci");
        assertEquals(bag.getBasePrice(), 2099);
        assertEquals(bag.getUsedBehaviour().getNumberOfOwners(), 5);
        assertEquals(bag.getUsedBehaviour().getStateOfUse(), 0.2);
        assertEquals(bag.getPremiumBehaviour().getMarkup(), 0.3);
        assertEquals(bag.getPremiumBehaviour().getLastUpdated(), 2010);
        assertEquals(bag.getCarrier(), carrier);
        assertEquals(bag.getDimensions(), dimensions);
        assertEquals(bag.getMaterial(), "neon leather");
        assertEquals(bag.getCollectionYear(), 2003);
        assertEquals(bag.getDiscount(), 0.15);
        assertEquals(bag.getPriceCorrection(), 2099 - ((2099.0/5)*0.2) - (1*2*3 * 0.15 * 2099)/100.0 + (2099*0.3*(2010-2003)), delta);
        assertEquals(bag.articleType(), "bag");
        assertTrue(bag.isUsed());
        assertTrue(bag.isPremium());
    }

    @Test
    void testTShirt() throws ArticleNegativeNumberException {
        TShirt shirt = new TShirt
                .TShirtBuilder()
                .setDescription("Nice T-Shirt")
                .setBrand("Gucci")
                .setBasePrice(1099)
                .setUsedBehaviour(10, 0.1)
                .setPremiumBehaviour(0.3, 2010)
                .setCarrier(carrier)
                .setId(uuid)
                .setSize(TShirt.Size.L)
                .setPattern(TShirt.Pattern.STRIPES)
                .buildWithoutPriceCorrection();

        shirt.updatePriceCorrection(2010);

        assertEquals(shirt.getDescription(), "Nice T-Shirt");
        assertEquals(shirt.getBrand(), "Gucci");
        assertEquals(shirt.getBasePrice(), 1099);
        assertEquals(shirt.getUsedBehaviour().getNumberOfOwners(), 10);
        assertEquals(shirt.getUsedBehaviour().getStateOfUse(), 0.1);
        assertEquals(shirt.getPremiumBehaviour().getMarkup(), 0.3);
        assertEquals(shirt.getPremiumBehaviour().getLastUpdated(), 2010);
        assertEquals(shirt.getCarrier(), carrier);
        assertEquals(shirt.getSize(), TShirt.Size.L);
        assertEquals(shirt.getPattern(), TShirt.Pattern.STRIPES);
        assertEquals(shirt.articleType(), "t-shirt");
        assertEquals(shirt.getPriceCorrection(), 538.51);

        assertTrue(shirt.isUsed());
        assertTrue(shirt.isPremium());
    }

    @Test
    void testShoes() throws ArticleNegativeNumberException {
        Shoes shoes = new Shoes
                .ShoesBuilder()
                .setDescription("Nice shoes")
                .setBrand("Nike")
                .setBasePrice(40.99)
                .setUsedBehaviour(5, 0.2)
                .setPremiumBehaviour(0.3, 2003)
                .setCarrier(carrier)
                .setId(uuid)
                .setSize(47)
                .setHasLaces(true)
                .setColor("red")
                .setCollectionYear(2003)
                .setDiscount(0.15)
                .buildWithoutPriceCorrection();

        shoes.updatePriceCorrection(2010);

        assertEquals(shoes.getDescription(), "Nice shoes");
        assertEquals(shoes.getBrand(), "Nike");
        assertEquals(shoes.getUsedBehaviour().getNumberOfOwners(), 5);
        assertEquals(shoes.getUsedBehaviour().getStateOfUse(), 0.2);
        assertEquals(shoes.getPremiumBehaviour().getMarkup(), 0.3);
        assertEquals(shoes.getPremiumBehaviour().getLastUpdated(), 2010);
        assertEquals(shoes.getCarrier(), carrier);
        assertEquals(shoes.getColor(), "red");
        assertTrue(shoes.hasLaces());
        assertEquals(shoes.getCollectionYear(), 2003);
        assertEquals(shoes.getDiscount(), 0.15);
        assertEquals(shoes.getBasePrice(), 40.99);
        assertEquals(shoes.getPriceCorrection(), 40.99 - (40.99*0.15) - ((40.99/5)*0.2) + (40.99*0.3*(2010-2003)), delta);
        assertEquals(shoes.articleType(), "shoes");
        assertTrue(shoes.isUsed());
        assertTrue(shoes.isPremium());
    }

    @Test
    void testTShirtPatternChange() throws ArticleNegativeNumberException {
        UsedBehaviour ub1 = new RegularUsed(10, 0.1);
        PremiumBehaviour pb1 = new RegularPremium(0.3, 2003);

        TShirt shirt = new TShirt("Nice T-Shirt", "Gucci", 1099, ub1, pb1, carrier, uuid, TShirt.Size.L, TShirt.Pattern.STRIPES);
        assertEquals(shirt.getPriceCorrection(),549.5);
        shirt.updatePriceCorrection(2010);
        assertEquals(shirt.getPriceCorrection(), 549.5 - ((1099.0/10.0) * 0.1));

        shirt.setPattern(TShirt.Pattern.SOLID);
        assertEquals(shirt.getPriceCorrection(),1088.01);
    }

    @Test
    void testShoesTimeLeap() throws ArticleNegativeNumberException {
        UsedBehaviour ub = new RegularUsed(5, 0.2);
        PremiumBehaviour pb = new RegularPremium(0.3, 2003); // lastUpdate is initially the collection year of the article

        Shoes shoes = new Shoes("Nice shoes", "Nike", 40.99, ub, pb, carrier, uuid, 47, true, "red", 2003, 0.15);
        shoes.updatePriceCorrection(2012);

        assertEquals(shoes.getBasePrice(), 40.99);
        assertEquals(shoes.getPriceCorrection(), 40.99 - (40.99*0.15) - ((40.99/5)*0.2) + (40.99*0.3*(2012-2003)), delta);

        shoes.updatePremiumPriceCorrection(2023);
        assertEquals(shoes.getPriceCorrection(), 40.99 - (40.99*0.15) - ((40.99/5)*0.2) + (40.99*0.3*(2023-2003)), delta);
    }

    @Test
    void testNotUsedNotPremiumShoes() throws ArticleNegativeNumberException {
        UsedBehaviour ub = new NotUsed();
        PremiumBehaviour pb = new NotPremium();

        Shoes shoes = new Shoes("Nice shoes", "Nike", 40.99, ub, pb, carrier, uuid,39, true, "red", 2003, 0.15);
        shoes.updatePriceCorrection(2012);
        assertEquals(shoes.getPriceCorrection(), 40.99);
        assertFalse(shoes.isPremium());
        assertFalse(shoes.isUsed());
    }

    @Test
    void testChangeShoesSizeToBigger() throws ArticleNegativeNumberException {
        UsedBehaviour ub = new NotUsed();
        PremiumBehaviour pb = new NotPremium();

        Shoes shoes = new Shoes("Nice shoes", "Nike", 40.99, ub, pb, carrier, uuid, 39, true, "red", 2003, 0.15);
        shoes.updatePriceCorrection(2012);

        assertEquals(shoes.getPriceCorrection(), 40.99);
        shoes.setSize(47);
        assertEquals(shoes.getPriceCorrection(), 40.99 - (40.99) * 0.15);
    }

    @Test
    void testChangeShoesSizeToSmaller() throws ArticleNegativeNumberException {
        UsedBehaviour ub = new NotUsed();
        PremiumBehaviour pb = new NotPremium();

        Shoes shoes = new Shoes("Nice shoes", "Nike", 40.99, ub, pb, carrier, uuid, 47, true, "red", 2003, 0.15);
        shoes.updatePriceCorrection(2012);

        assertEquals(shoes.getPriceCorrection(), 40.99 - (40.99) * 0.15);
        shoes.setSize(39);
        assertEquals(shoes.getPriceCorrection(), 40.99);
    }

    @Test
    void testChangeShoesCollectionYear() throws ArticleNegativeNumberException {
        UsedBehaviour ub = new RegularUsed(5, 0.2);
        PremiumBehaviour pb = new RegularPremium(0.3, 2003); // lastUpdate is initially the collection year of the article

        Shoes shoes = new Shoes("Nice shoes", "Nike", 40.99, ub, pb, carrier, uuid, 47, true, "red", 2003, 0.15);
        shoes.updatePriceCorrection(2012);

        assertEquals(shoes.getPriceCorrection(), 40.99 - (40.99*0.15) - ((40.99/5)*0.2) + (40.99*0.3*(2012-2003)), delta);
        shoes.setCollectionYear(2004);
        assertEquals(shoes.getPriceCorrection(), 40.99 - (40.99*0.15) - ((40.99/5)*0.2) + (40.99*0.3*(2012-2004)), delta);
        shoes.setCollectionYear(2002);
        assertEquals(shoes.getPriceCorrection(), 40.99 - (40.99*0.15) - ((40.99/5)*0.2) + (40.99*0.3*(2012-2002)), delta);
    }

    @Test
    void testChangeBagCollectionYear() throws ArticleNegativeNumberException {

        UsedBehaviour ub = new RegularUsed(5, 0.2);
        PremiumBehaviour pb = new RegularPremium(0.3, 2003);

        int[] dimensions = {1,2,3};

        Bag bag = new Bag("Nice bag", "Gucci", 2099, ub, pb, carrier, uuid, dimensions, "neon leather", 2003, 0.15);
        bag.updatePriceCorrection(2012);
        assertEquals(bag.getPriceCorrection(), 2099 - ((2099.0/5)*0.2) - (1*2*3 * 0.15 * 2099)/100.0 + (2099*0.3*(2012-2003)), delta);

        bag.setCollectionYear(2005);
        assertEquals(bag.getPriceCorrection(), 2099 - ((2099.0/5)*0.2) - (1*2*3 * 0.15 * 2099)/100.0 + (2099*0.3*(2012-2005)), delta);

        bag.updatePremiumPriceCorrection(2023); // time leap
        assertEquals(bag.getPriceCorrection(), 2099 - ((2099.0/5)*0.2) - (1*2*3 * 0.15 * 2099)/100.0 + (2099*0.3*(2023-2005)), delta);
    }

    @Test
    void testBagTimeLeap() throws ArticleNegativeNumberException {
        UsedBehaviour ub = new RegularUsed(5, 0.2);
        PremiumBehaviour pb = new RegularPremium(0.3, 2003);

        int[] dimensions = {1,2,3};

        Bag bag = new Bag("Nice bag", "Gucci", 2099, ub, pb, carrier, uuid, dimensions, "neon leather", 2003, 0.15);
        bag.updatePriceCorrection(2012);

        bag.updatePremiumPriceCorrection(2023); // premium
        assertEquals(bag.getPriceCorrection(), 2099 - ((2099.0/5)*0.2) - (1*2*3 * 0.15 * 2099)/100.0 + (2099*0.3*(2023-2003)), delta);
    }

    @Test
    void testChangeBagDimensions() throws ArticleNegativeNumberException {
        UsedBehaviour ub = new NotUsed();
        PremiumBehaviour pb = new NotPremium();

        int[] dimensions = {1,2,3};

        Bag bag = new Bag("Nice bag", "Gucci", 2099, ub, pb, carrier, uuid, dimensions, "neon leather", 2003, 0.15);
        bag.updatePriceCorrection(2012);

        assertEquals(bag.getPriceCorrection(), 2099 - (1*2*3 * 0.15 * 2099)/100.0);

        int[] dimensions2 = {1,3,3};
        bag.setDimensions(dimensions2);
        assertEquals(bag.getPriceCorrection(), 2099 - (1*3*3 * 0.15 * 2099)/100.0);

        bag.setDimension(0, 3);
        assertEquals(bag.getPriceCorrection(), 2099 - (3*3*3 * 0.15 * 2099)/100.0);
    }

    @Test
    void testAddIdToArticle() {
        Article shoes = new Shoes
                .ShoesBuilder()
                .setDescription("Nice shoes")
                .setBrand("Nike")
                .setBasePrice(40.99)
                .setUsedBehaviour(5, 0.2)
                .setPremiumBehaviour(0.3, 2003)
                .setCarrier(carrier)
                .setId(uuid)
                .setSize(47)
                .setHasLaces(true)
                .setColor("red")
                .setCollectionYear(2003)
                .setDiscount(0.15)
                .buildWithoutPriceCorrection();

        ArticleCatalog articleCatalog = new ArticleCatalog();
        articleCatalog.add(shoes);

        assertEquals(articleCatalog.get(shoes.getId()), shoes);
    }

}