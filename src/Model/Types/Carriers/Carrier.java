package Model.Types.Carriers;

import Logs.AppLogger;
import Model.Types.HasId;
import Model.Types.Order;
import Model.Types.Package;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Carrier implements HasId, Serializable, Cloneable {
    private UUID id;
    private String name;
    private double basePriceSmall; // 1 article
    private double basePriceMedium; // 2 - 5 articles
    private double basePriceBig; // more than 5 articles
    private double tax;
    private double profitRate;
    private double totalMoneyEarned;
    private Map<UUID, Package> packages; // order id -> package

    public Carrier (Carrier carrier) {
        this.name = carrier.getName();
        this.id = carrier.getId();
        this.basePriceSmall = carrier.getBasePriceSmall();
        this.basePriceMedium = carrier.getBasePriceMedium();
        this.basePriceBig = carrier.getBasePriceBig();
        this.tax = carrier.getTax();
        this.profitRate = carrier.getProfitRate();
        this.packages = carrier.getPackages();
        this.totalMoneyEarned = carrier.getTotalMoneyEarned();
    }

    public Carrier (String name, double basePriceSmall, double basePriceMedium, double basePriceBig, double tax, double profitRate) {
        this.name = name;
        this.basePriceSmall = basePriceSmall;
        this.basePriceMedium = basePriceMedium;
        this.basePriceBig = basePriceBig;
        this.tax = tax;
        this.profitRate = profitRate;
        this.packages = new HashMap<>();
        this.totalMoneyEarned = 0;
    }

    public void addPackage (Order order, Package p, LocalDateTime now) {
        if (!this.packages.containsKey(order.getId())) {
            this.setPackageDates(p, now);
            this.packages.put(order.getId(), p);
        } // an order an only have one package per carrier
    }

    public void removePackage(UUID orderID) {
        this.packages.remove(orderID);
    }

    public void setPackageDates (Package p, LocalDateTime now) {
        LocalDateTime sentDate = this.decideDate(now);
        LocalDateTime arrivedDate = this.decideDate(sentDate);

        p.setSentDate(sentDate);
        p.setArrivedDate(arrivedDate);
    }

    private LocalDateTime decideDate (LocalDateTime date) {
        int randomHours = ThreadLocalRandom.current().nextInt(24, 49);
        return date.plus(randomHours, ChronoUnit.HOURS);
    }

    public UUID getId() {
        return this.id;
    }
    public void setId (UUID id) {
        this.id = id;
    }
    public double getBasePriceSmall() {
        return basePriceSmall;
    }
    public double getBasePriceMedium() {
        return basePriceMedium;
    }
    public double getBasePriceBig() {
        return basePriceBig;
    }
    public double getTax() {
        return tax;
    }
    public double getProfitRate() {
        return profitRate;
    }
    public String getName() {
        return name;
    }
    public double getTotalMoneyEarned() {
        return this.totalMoneyEarned;
    }
    public void addToMoneyEarned (double value) {
        this.totalMoneyEarned += value;
    }

    public Map<UUID, Package> getPackages() {
        return this.packages;
    }
    public void setName (String name) {
        this.name = name;
    }
    public void setBasePriceSmall(double basePriceSmall) {
        this.basePriceSmall = basePriceSmall;
    }
    public void setBasePriceMedium(double basePriceMedium) {
        this.basePriceMedium = basePriceMedium;
    }
    public void setBasePriceBig(double basePriceBig) {
        this.basePriceBig = basePriceBig;
    }
    public void setTax(double tax) {
        this.tax = tax;
    }
    public void setProfitRate(double profitRate) {
        this.profitRate = profitRate;
    }

    public abstract String carrierType();
    public abstract double calculateShippingCost(Package p);
    public abstract void setProfit(Package p);

    @Override
    public String toString() {
        return "{ name = '" + name + '\'' +
                ", basePriceSmall = " + basePriceSmall +
                ", basePriceMedium = " + basePriceMedium +
                ", basePriceBig = " + basePriceBig +
                ", tax = " + tax +
                ", profitRate = " + profitRate +
                ", moneyEarned = " + totalMoneyEarned +
                ", type = " + carrierType() +
                ", packages = " + this.packages +
                " }";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || (obj.getClass() != this.getClass())) return false;
        Carrier c = (Carrier) obj;
        return this.id.equals(c.getId());
    }

    @Override
    public Carrier clone() {
        try {
            return (Carrier) super.clone();
        } catch (CloneNotSupportedException e) {
            AppLogger.logError(e);
            throw new AssertionError();
        }
    }
}
