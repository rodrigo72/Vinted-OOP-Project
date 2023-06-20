package Model.Data.Catalogs;

import Model.Types.Order;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class OrderCatalog extends GenericCatalog<Order> {
    public OrderCatalog() {
        super();
    }
    public OrderCatalog (Map<UUID, Order> orders) {
        super(orders);
    }
    public OrderCatalog (OrderCatalog orderCatalog) {
        this.catalog = orderCatalog.getCatalog();
    }

    @Override
    public String toString() {
        return "OrderCatalog {" +
                " orders = " + this.catalog +
                " }";
    }

    // time passing effect on all orders
    public void notifyAllOrders (LocalDateTime now) {
        for (Order order : this.catalog.values()) {
            order.send(now);
            order.arrived(now);
        }
    }
}
