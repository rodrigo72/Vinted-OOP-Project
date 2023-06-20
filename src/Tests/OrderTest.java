import Model.Types.Articles.Bag;
import Model.Types.Articles.TShirt;
import Model.Types.Carriers.Carrier;
import Model.Types.Exceptions.ArticleNegativeNumberException;
import Model.Types.Order;
import Model.Types.Carriers.RegularCarrier;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private double delta = 0.00001;
    int[] dimensions = {1, 2, 3};
    UUID idArticle1 = UUID.randomUUID();
    UUID idArticle2 = UUID.randomUUID();
    UUID idOrder1 = UUID.randomUUID();
    Carrier carrier1 = new RegularCarrier("CTT", 3, 5, 10, 0.23, 0.05);
    Carrier carrier2 = new RegularCarrier("LaCasaRosaExpress", 3.5, 6, 12, 0.23, 0.1);

    @Test
    void testOrder() throws ArticleNegativeNumberException {

        carrier1.setId(UUID.randomUUID());
        carrier2.setId(UUID.randomUUID());

        Order order = new Order(idOrder1, LocalDateTime.now());
        Bag bag = new Bag
                .BagBuilder()
                .setDescription("Nice bag")
                .setBrand("Gucci")
                .setBasePrice(2099)
                .setUsedBehaviour(5, 0.2)
                .setPremiumBehaviour(0.3, 2003)
                .setCarrier(carrier1)
                .setId(idArticle1)
                .setDimensions(dimensions)
                .setMaterial("neon leather")
                .setCollectionYear(2003)
                .setDiscount(0.15)
                .buildWithoutPriceCorrection();

        bag.updatePriceCorrection(2010);

        TShirt shirt = new TShirt
                .TShirtBuilder()
                .setDescription("Nice T-Shirt")
                .setBrand("Gucci")
                .setBasePrice(1099)
                .setUsedBehaviour(10, 0.1)
                .setPremiumBehaviour(0.3, 2010)
                .setCarrier(carrier2)
                .setId(idArticle2)
                .setSize(TShirt.Size.L)
                .setPattern(TShirt.Pattern.STRIPES)
                .buildWithoutPriceCorrection();

        shirt.updatePriceCorrection(2010);

        order.addArticle(bag);
        order.addArticle(shirt);
        order.calculateFinalPrice();

        assertEquals(order.getFinalPrice(), (6404.049 + 0.25 + 3*(1+0.23+0.05)) +
                        (538.51 + 0.25 + 3.5*(1+0.23+0.1))
                , delta);
    }
}