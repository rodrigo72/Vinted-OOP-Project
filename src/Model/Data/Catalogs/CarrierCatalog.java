package Model.Data.Catalogs;

import Model.Data.Exceptions.AlreadyExistsInCatalogException;
import Model.Types.Carriers.Carrier;
import Model.Types.Carriers.PremiumCarrier;
import Model.Types.Carriers.RegularCarrier;

import java.util.*;

public class CarrierCatalog extends GenericCatalog<Carrier> {
    private Map<String, Carrier> usedNames;
    public CarrierCatalog() {
        super();
        this.usedNames = new HashMap<>();
    }
    public CarrierCatalog (Map<UUID, Carrier> carriers) {
        super(carriers);
        this.usedNames = new HashMap<>();
    }
    public CarrierCatalog (CarrierCatalog carrierCatalog) {
        this.catalog = carrierCatalog.getCatalog();
        this.usedNames = carrierCatalog.getUsedNames();
    }

    public Map<String, Carrier> getUsedNames() {
        return this.usedNames;
    }

    public void setUsedNames(Map<String, Carrier> usedNames) {
        this.usedNames = usedNames;
    }

    @Override
    public String toString() {
        return "CarrierCatalog {" +
                " carriers = " + this.catalog +
                " }";
    }

    public List<String> getNamesList() {
        Set<String> namesSet = this.usedNames.keySet();
        return new ArrayList<>(namesSet);
    }

    @Override
    public void add (Carrier carrier) {
        if (!this.usedNames.containsKey(carrier.getName())) {
            super.add(carrier);
            this.usedNames.put(carrier.getName(), carrier);
        }
    }

    private double checkPercentage (double value) {
        double finalValue = -1;
        if (value <= 1 && value >= 0) return value;
        if (value > 1 && value/100 <= 1) return value/100;

        return finalValue;
    }

    private Carrier createCarrier (String name, double basePriceSmall, double basePriceMedium, double basePriceBig, double tax, double profitRate, Double addedPrice) throws AlreadyExistsInCatalogException {
        double finalTax = this.checkPercentage(tax);
        if (finalTax == -1) return null;

        double finalProfit = this.checkPercentage(profitRate);
        if (finalProfit == -1) return null;

        if (exists(name)) {
            throw new AlreadyExistsInCatalogException();
        }

        if (addedPrice == null) {
            return new RegularCarrier(name, basePriceSmall, basePriceMedium, basePriceBig, tax, profitRate);
        } else {
            return new PremiumCarrier(name, basePriceSmall, basePriceMedium, basePriceBig, tax, profitRate, addedPrice);
        }
    }

    public void add (String name, double basePriceSmall, double basePriceMedium, double basePriceBig, double tax, double profitRate) throws AlreadyExistsInCatalogException {
        Carrier carrier = createCarrier(name, basePriceSmall, basePriceMedium, basePriceBig, tax, profitRate, null);
        super.add(carrier);
        this.usedNames.put(name, carrier);
    }

    public void add (String name, double basePriceSmall, double basePriceMedium, double basePriceBig, double tax, double profitRate, double addedPrice) throws AlreadyExistsInCatalogException {
        Carrier carrier = createCarrier(name, basePriceSmall, basePriceMedium, basePriceBig, tax, profitRate, addedPrice);
        super.add(carrier);
        this.usedNames.put(name, carrier);
    }


    public Carrier getRichestCarrier() {
        double highest = -0.01;
        Carrier chosen = null;
        for (Carrier carrier : this.catalog.values()) {
            if (carrier.getTotalMoneyEarned() > highest) {
                highest = carrier.getTotalMoneyEarned();
                chosen = carrier;
            }
        }
        return chosen;
    }

    public boolean exists (String name) {
        return this.usedNames.containsKey(name);
    }
    public Carrier get (String name) {
        return this.usedNames.get(name);
    }
}
