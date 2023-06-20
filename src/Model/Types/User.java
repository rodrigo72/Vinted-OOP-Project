package Model.Types;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class User implements HasId, Serializable {
    private UUID id;
    private String email;
    private String name;
    private Residence residence;
    private double moneyEarned;
    private double moneySpent;
    private Map<Long, Double> moneySpentByDay;
    private Map<Long, Double> moneyEarnedByDay;

    public User (String name, String email, Residence residence) {
        this.email = email;
        this.name = name;
        this.residence = residence.clone();
        this.moneySpent = 0;
        this.moneyEarned = 0;
        this.moneySpentByDay = new HashMap<>();
        this.moneyEarnedByDay = new HashMap<>();
    }

    public User (User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.residence = user.getResidence();
        this.moneyEarned = user.getMoneyEarned();
        this.moneySpent = user.getMoneySpent();
        this.moneySpentByDay = user.getMoneySpentByDay();
        this.moneyEarnedByDay = user.getMoneyEarnedByDay();
    }

    public UUID getId() {
        return this.id;
    }
    public void setId (UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Residence getResidence() {
        return residence.clone();
    }
    public void setResidence(Residence residence) {
        this.residence = residence.clone();
    }

    public double getMoneyEarned() {
        return this.moneyEarned;
    }
    public void setMoneyEarned(double moneyEarned) {
        this.moneyEarned = moneyEarned;
    }
    public double getMoneySpent() {
        return this.moneySpent;
    }
    public void setMoneySpent(double moneySpent) {
        this.moneySpent = moneySpent;
    }

    public Map<Long, Double> getMoneySpentByDay() {
        return moneySpentByDay;
    }
    public void setMoneySpentByDay(Map<Long, Double> moneySpentByDay) {
        this.moneySpentByDay = moneySpentByDay;
    }
    public Map<Long, Double> getMoneyEarnedByDay() {
        return moneyEarnedByDay;
    }
    public void setMoneyEarnedByDay(Map<Long, Double> getMoneyEarnedByDay) {
        this.moneyEarnedByDay = getMoneyEarnedByDay;
    }

    public void setCountry (String country) {
        this.residence.setCountry(country);
    }
    public void setCity (String city) {
        this.residence.setCity(city);
    }
    public void setStreet (String street) {
        this.residence.setStreet(street);
    }
    public void setPostalCode (String postalCode) { this.residence.setPostalCode(postalCode); }

    @Override
    public String toString() {
        return "User {" +
                // "id =" + id +
                "email = '" + email + '\'' +
                ", name = '" + name + '\'' +
                ", residence = " + residence +
                ", money earned = " + moneyEarned +
                ", money spent = " + moneySpent +
                " }";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || (obj.getClass() != this.getClass())) return false;
        User u = (User) obj;
        return this.id.equals(u.getId());
    }

    public void addToMoneyEarned (double earned, LocalDate date) {
        this.moneyEarned += earned;
        this.addToMoney(earned, date, this.moneyEarnedByDay);
    }

    public void addToMoneySpent (double spent, LocalDate date) {
        this.moneySpent += spent;
        this.addToMoney(spent, date, this.moneySpentByDay);
    }

    private void addToMoney (double amount, LocalDate date, Map<Long, Double> moneyByDay) {
        long epoch = date.toEpochDay();
        if (moneyByDay.containsKey(epoch)) {
            Double money = moneyByDay.get(epoch);
            money += amount;
            moneyByDay.put(epoch, money);
        } else {
            moneyByDay.put(epoch, amount);
        }
    }

    public void removeFromMoneyEarned (double value, LocalDate date) {
        this.moneyEarned -= value;
        this.removeFromMoney(value, date, this.moneyEarnedByDay);
    }

    public void removeFromMoneySpent (double value, LocalDate date) {
        this.moneySpent -= value;
        this.removeFromMoney(value, date, this.moneySpentByDay);
    }

    // to reuse code
    private void removeFromMoney (double value, LocalDate date, Map<Long, Double> moneyByDay) {
        long epoch = date.toEpochDay();
        if (moneyByDay.containsKey(epoch)) {
            Double money = moneyByDay.get(epoch);
            money -= value;
            if (money > 0) moneyByDay.put(epoch, money);
            else moneyByDay.remove(epoch);
        }
    }

    public double getMoneyEarnedInPeriodOfTime (LocalDate infDate, LocalDate supDate) {
        return this.getMoneyInPeriodOfTime(this.moneyEarnedByDay, infDate, supDate);
    }

    public double getMoneySpentInPeriodOfTime (LocalDate infDate, LocalDate supDate) {
        return this.getMoneyInPeriodOfTime(this.moneySpentByDay, infDate, supDate);
    }

    private double getMoneyInPeriodOfTime (Map<Long, Double> map, LocalDate infDate, LocalDate supDate) {
        double acc = 0.0;
        long infEpoch = infDate.toEpochDay();
        long supEpoch = supDate.toEpochDay();

        for (long epoch = infEpoch; epoch <= supEpoch; epoch++) {
            if (map.containsKey(epoch)) {
                acc += map.get(epoch);
            }
        }

        return acc;
    }
}
