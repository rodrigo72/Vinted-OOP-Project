package Model.Types;

import Model.Types.Articles.Article;
import Model.Types.Carriers.Carrier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Package implements Serializable {

    public enum Status {
        NOT_SENT, SENT, ARRIVED, RETURNED
    }

    private static final double NEW_ARTICLE_FEE = 0.5;
    private static final double USED_ARTICLE_FEE = 0.25;
    private double vintageProfit;
    private Carrier carrier;
    private ArrayList<Article> articles;
    private Status status;
    private int numberOfArticles;

    private LocalDateTime sentDate;
    private LocalDateTime arrivedDate;

    public Package (Carrier carrier) {
        this.carrier = carrier;
        this.status = Status.NOT_SENT;
        this.articles = new ArrayList<>();
        this.numberOfArticles = 0;
        this.vintageProfit = 0;
        this.sentDate = null;
        this.arrivedDate = null;
    }

    public Package (Carrier carrier, ArrayList<Article> articles) {
        this.carrier = carrier;
        this.status = Status.NOT_SENT;
        this.articles = articles;
        this.numberOfArticles = 0;
        this.vintageProfit = 0;
    }

    public Package (Package p) {
        this.carrier = p.getCarrier();
        this.articles = p.getArticlesDeepCopy();
        this.status = p.getStatus();
        this.numberOfArticles = p.getNumberOfArticles();
        this.sentDate = p.getSentDate();
        this.arrivedDate = p.getArrivedDate();
        this.vintageProfit = p.getVintageProfit();
    }

    public ArrayList<Article> getArticles() {
        return articles;
    } // without clone
    public ArrayList<Article> getArticlesDeepCopy() {
        return this.makeArticlesDeepCopy(this.articles);
    }
    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }
    public Carrier getCarrier() {
        return carrier;
    }
    public LocalDateTime getSentDate() {
        return this.sentDate;
    }
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }
    public Status getStatus() {
        return status;
    }
    public LocalDateTime getArrivedDate() {
        return this.arrivedDate;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public int getNumberOfArticles() {
        return this.numberOfArticles;
    }
    public double getVintageProfit() { return this.vintageProfit; }
    public void setNumberOfArticles (int numberOfArticles) {
        this.numberOfArticles = numberOfArticles;
    }
    public void setSentDate (LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }
    public void setArrivedDate (LocalDateTime arrivedDate) {
        this.arrivedDate = arrivedDate;
    }

    private ArrayList<Article> makeArticlesDeepCopy (ArrayList<Article> articles) {
         return articles.stream().map(Article::clone).collect(Collectors.toCollection(ArrayList::new));
    }

    public void addArticle (Article article) {
        this.articles.add(article);
        this.addVintageProfit(article);
        this.numberOfArticles++;
    }

    private void addVintageProfit (Article article) {
        if (article.isUsed()) this.vintageProfit += USED_ARTICLE_FEE;
        else this.vintageProfit += NEW_ARTICLE_FEE;
    }

    private void removeVintageProfit (Article article) {
        if (article.isUsed()) this.vintageProfit -= USED_ARTICLE_FEE;
        else this.vintageProfit -= NEW_ARTICLE_FEE;
    }

    public boolean hasArticle (Article article) {
        return this.articles.contains(article);
    }

    public boolean removeArticle (Article article) {
        boolean removed = false;
        Iterator<Article> it = this.articles.iterator();
        while (it.hasNext() && !removed) {
            Article a = it.next();
            if (a.equals(article)) {
                it.remove();
                removed = true;
                this.numberOfArticles--;
                this.removeVintageProfit(article);
            }
        }

        if (this.numberOfArticles == 0) {
            return true;
        }
        return false;
    }

    public double calculatePackagePrice() {
        double price = 0;
        price += this.carrier.calculateShippingCost(this);
        for (Article article : this.articles) {
            price += article.getPriceCorrection();
        }
        return price + this.vintageProfit;
    }

    public boolean send (LocalDateTime now) {
        if (now.isAfter(this.sentDate)) {
            this.status = Status.SENT;
            return true;
        } else {
            return false;
        }
    }

    public boolean arrived (LocalDateTime now) {
        if (now.isAfter(this.arrivedDate)) {
            this.status = Status.ARRIVED;
            return true;
        } else {
            return false;
        }
    }

    public boolean hasArrived() {
        return this.status.equals(Status.ARRIVED);
    }

    public boolean isSent() {
        return this.status.equals(Status.SENT) || this.hasArrived();
    }

    public void setAsReturned() {
        if (this.hasArrived()) {
            this.status = Status.RETURNED;
        }
    }

    public void setCarrierProfit() {
        this.carrier.setProfit(this);
    }

    @Override
    public Package clone() {
        return new Package(this);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return  "{" + carrier.carrierType() + " carrier = " + carrier.getName() +
                ", status = " + status +
                ", sentDate = " + sentDate.format(formatter) +
                ", arrivedDate = " + arrivedDate.format(formatter) +
                ", numberOfArticles = " + numberOfArticles + '}';
    }
}
