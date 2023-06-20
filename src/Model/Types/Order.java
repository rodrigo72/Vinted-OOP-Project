package Model.Types;

import Model.Types.Articles.Article;
import Model.Types.Articles.ArticleInfo;
import Model.Types.Carriers.Carrier;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Order implements HasId, Serializable {

    public enum State {
        PENDING, FINALIZED, SENT, ARRIVED, RETURNED
    }

    private UUID id;
    private UUID buyerId;
    private Map<UUID, Package> packages;
    private double finalPrice;
    private State state;
    private LocalDateTime creationDate;
    private LocalDateTime finalizedDate;
    private LocalDateTime arrivedDate;
    private int numberOfArticles;

    public UUID getId() {
        return this.id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public Order (UUID buyerId, LocalDateTime now) {
        this.state = State.PENDING;
        this.buyerId = buyerId;
        this.finalPrice = 0;
        this.numberOfArticles = 0;
        this.packages = new HashMap<>();
        this.creationDate = now;
        this.arrivedDate = null;
        this.finalizedDate = null;
    }

    public Order (State state, UUID buyerId) {
        this.state = state;
        this.buyerId = buyerId;
        this.finalPrice = 0;
        this.numberOfArticles = 0;
        this.packages = new HashMap<>();
        this.creationDate = LocalDateTime.now();
    }

    public Order (UUID id, State state, UUID buyerId, Map<UUID, Package> packages,
                  double finalPrice, LocalDateTime creationDate, int numberOfArticles) {
        this.id = id;
        this.state = state;
        this.buyerId = buyerId;
        this.packages = packages;
        this.finalPrice = finalPrice;
        this.creationDate = creationDate;
        this.numberOfArticles = numberOfArticles;
    }

    public Order (Order order) {
        this.id = order.getId();
        this.state = order.getState();
        this.buyerId = order.getBuyerId();
        this.packages = this.packagesDeepCopy(order.getPackages());
        this.finalPrice = order.getFinalPrice();
        this.creationDate = order.getCreationDate();
        this.arrivedDate = order.getArrivedDate();
        this.numberOfArticles = order.getNumberOfArticles();
        this.finalizedDate = order.getFinalizedDate();
    }

    public Map<UUID, Package> getPackages() {
        return this.packages;
    }
    public UUID getBuyerId() {
        return buyerId;
    }
    public double getFinalPrice() {
        return finalPrice;
    }
    public State getState() {
        return this.state;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public int getNumberOfArticles() {
        return numberOfArticles;
    }
    public LocalDateTime getArrivedDate() {
        return this.arrivedDate;
    }
    public LocalDateTime getFinalizedDate() {
        return this.finalizedDate;
    }

    public void setBuyerId (UUID buyerId) {
        this.buyerId = buyerId;
    }
    public void setFinalPrice (double finalPrice) {
        this.finalPrice = finalPrice;
    }
    public void setState (State state) {
        this.state = state;
    }
    public void setCreationDate (LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    public void setNumberOfArticles (int numberOfArticles) {
        this.numberOfArticles = numberOfArticles;
    }
    public void setPackages (Map<UUID, Package> packages) {
        this.packages = packages;
    }

    // equivalent to buying the order
    public void setAsFinalized (LocalDateTime now) {
        this.state = State.FINALIZED;
        this.finalizedDate = now;
        for (Package p : this.packages.values()) {
            Carrier carrier = p.getCarrier();
            carrier.addPackage(this, p, now);
        }
    }

    private Map<UUID, Package> packagesDeepCopy (Map<UUID, Package> packages) {
        Map<UUID, Package> copy = new HashMap<>();

        for (Map.Entry<UUID, Package> entry : packages.entrySet()) {
            UUID keyId = entry.getKey();
            Package p = entry.getValue();
            copy.put(keyId, p.clone());
        }
        return copy;
    }

    public void addArticle (Article article) {
        UUID carrierID = article.getCarrierID();
        if (this.packages.containsKey(carrierID)) {
            this.packages.get(carrierID).addArticle(article);
        } else {
            Package p = new Package(article.getCarrier());
            p.addArticle(article);
            this.packages.put(carrierID, p);
        }
        this.calculateFinalPrice();
        this.numberOfArticles++;
    }

    public ArrayList<ArticleInfo> getArticlesInfo () {
        ArrayList<ArticleInfo> info = new ArrayList<>();
        for (Package pac : this.packages.values()) {
            for (Article article : pac.getArticles()) {
                ArticleInfo articleInfo = new ArticleInfo(article.getSellerId(), article.getId(), article.toString());
                info.add(articleInfo);
            }
        }
        return info;
    }

    public boolean hasArticle (Article article) {
        UUID carrierID = article.getCarrierID();
        if (this.packages.containsKey(carrierID)) {
            return this.packages.get(carrierID).hasArticle(article);
        }
        return false;
    }

    public void removeArticle (Article article) {
        if (this.packages.containsKey(article.getCarrierID())) {
            Package p = this.packages.get(article.getCarrierID());
            if (p.removeArticle(article)) {
                this.packages.remove(article.getCarrierID());
            }
            this.calculateFinalPrice();
            this.numberOfArticles--;
        }
    }

    public void calculateFinalPrice() {
        double price = 0;
        for (Map.Entry<UUID, Package> entry : packages.entrySet()) {
            Package p = entry.getValue();
            price += p.calculatePackagePrice();
        }

        this.finalPrice = price;
    }

    public void setCarriersProfit() {
        for (Package p : this.packages.values()) {
            p.setCarrierProfit();
        }
    }

    public ArrayList<Article> getArticles() { // without clone
        ArrayList<Article> allArticles = new ArrayList<>();
        for (Package p : this.packages.values()) {
            allArticles.addAll(p.getArticles());
        }
        return allArticles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(state).append(")\n");
        sb.append("Packages:\n");
        for (Package p : this.packages.values()) {
            sb.append("\t");
            sb.append(p.toString());
            sb.append("\n");
        }
        sb.append("Price = ").append(finalPrice).append("\n");
        sb.append("Creation date = ").append(creationDate).append("\n");

        return sb.toString();
    }

    @Override
    public Order clone() {
        return new Order(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return order.getId().equals(this.id);
    }

    public void send (LocalDateTime now) {
            if (this.state.equals(State.FINALIZED)) {
            boolean allPackagesSent = true;
            for (Package p : this.packages.values()) {
                if (!p.send(now)) allPackagesSent = false;
            }
            if (allPackagesSent) this.state = State.SENT;
        }
    }

    public void arrived (LocalDateTime now) {
        if (this.state.equals(State.SENT)) {
            LocalDateTime arrived = this.creationDate;
            boolean allPackagesArrived = true;
            for (Package p : this.packages.values()) {
                if (!p.arrived(now)) {
                    allPackagesArrived = false;
                } else {
                    LocalDateTime pArrivedDate = p.getArrivedDate();
                    if (pArrivedDate.isAfter(arrived)) {
                        arrived = pArrivedDate;
                        this.arrivedDate = arrived;
                    }
                }
            }
            if (allPackagesArrived) this.state = State.ARRIVED;
        }
    }

    public boolean isSent() {
        return this.state.equals(State.SENT);
    }

    public boolean notSent() {
        return this.state.equals(State.FINALIZED);
    }

    public boolean hasArrived() {
        return this.state.equals(State.ARRIVED);
    }

    public boolean wasReturned() {
        return this.state.equals(State.RETURNED);
    }

    public boolean isCancelable() {
        for (Package p : this.packages.values()) {
            if (p.isSent()) return false;
        }
        return true;
    }

    public boolean isReturnable (LocalDateTime now) {
        int returnLimit = 48;
        return this.state.equals(State.ARRIVED) && Duration.between(this.arrivedDate, now).toHours() < returnLimit;
    }

    public void setAsReturned (LocalDateTime now) {
        if (this.isReturnable(now)) {
            this.state = State.RETURNED;
            for (Package p : this.packages.values()) {
                p.setAsReturned();
            }
        }
    }

    public void removePackages() {
        for (Package p : this.packages.values()) {
            Carrier carrier = p.getCarrier();
            carrier.removePackage(this.id);
            this.packages.remove(p);
        }
    }

    public double getVintageProfit() {
        double profit = 0;
        for (Package p : this.packages.values()) {
            profit += p.getVintageProfit();
        }
        return profit;
    }
}
