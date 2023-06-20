package Model.Types;

import java.io.Serializable;

public class Residence implements Serializable {
    private String country;
    private String city;
    private String street;
    private String postalCode;

    public Residence (Residence residence) {
        this.country = residence.getCountry();
        this.city = residence.getCity();
        this.street = residence.getStreet();
        this.postalCode = residence.getPostalCode();
    }

    public Residence(String country, String city, String street, String postalCode) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "Residence { " +
                "country = '" + country + '\'' +
                ", city = '" + city + '\'' +
                ", street = '" + street + '\'' +
                ", postalCode = '" + postalCode + '\'' +
                " }";
    }

    @Override
    public Residence clone() {
        return new Residence(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || (obj.getClass() != this.getClass())) return false;
        Residence r = (Residence) obj;
        return this.country.equals(r.getCountry()) && this.city.equals(r.getCity())
                && this.street.equals(r.getStreet()) && this.postalCode.equals(r.getPostalCode());
    }
}

