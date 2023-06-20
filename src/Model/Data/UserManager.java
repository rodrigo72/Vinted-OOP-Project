package Model.Data;

import Model.Types.Articles.Article;
import Model.Types.Articles.ArticleInfo;
import Model.Types.Order;
import Model.Types.OrderInfo;
import Model.Types.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class UserManager implements Serializable {
    private Map<UUID, ArrayList<Article>> soldArticles; // sellerID    -> Articles
    private Map<UUID, ArrayList<Article>> forSaleArticles; // sellerID -> Articles
    private Map<UUID, ArrayList<Article>> boughtArticles; // buyerID   -> Articles
    private Map<UUID, ArrayList<Order>> userOrders; // buyerID -> Orders
    private Map<UUID, Order> userCart; // buyerID -> order

    public UserManager() {
        this.soldArticles = new HashMap<>();
        this.forSaleArticles = new HashMap<>();
        this.boughtArticles = new HashMap<>();
        this.userOrders = new HashMap<>();
        this.userCart = new HashMap<>();
    }

    public UserManager(UserManager manager) {
        this.soldArticles = manager.getSoldArticles();
        this.forSaleArticles = manager.getForSaleArticles();
        this.boughtArticles = manager.getBoughtArticles();
        this.userOrders = manager.getUserOrders();
        this.userCart = manager.getUserCart();
    }

    public Map<UUID, ArrayList<Article>> getSoldArticles() { return soldArticles; }
    public void setSoldArticles(Map<UUID, ArrayList<Article>> soldArticles) { this.soldArticles = soldArticles; }
    public Map<UUID, ArrayList<Article>> getForSaleArticles() { return forSaleArticles; }
    public void setForSaleArticles(Map<UUID, ArrayList<Article>> forSaleArticles) { this.forSaleArticles = forSaleArticles;}
    public Map<UUID, ArrayList<Article>> getBoughtArticles() { return boughtArticles; }
    public void setBoughtArticles(Map<UUID, ArrayList<Article>> boughtArticles) { this.boughtArticles = boughtArticles;}
    public Map<UUID, ArrayList<Order>> getUserOrders() { return userOrders; }
    public Map<UUID, Order> getUserCart() { return userCart; }
    public void setUserOrders(Map<UUID, ArrayList<Order>> userOrders) { this.userOrders = userOrders;}

    public void addSoldArticle (User user, Article article) { this.addArticleToMap(this.soldArticles, user, article); }
    public void addSoldArticle (UUID id, Article article) { this.addArticleToMap(this.soldArticles, id, article.clone()); }
    public void addForSaleArticle (User user, Article article) { this.addArticleToMap(this.forSaleArticles, user, article); }
    public void addForSaleArticle (UUID id, Article article) { this.addArticleToMap(this.forSaleArticles, id, article); }
    public void addBoughtArticle (User user, Article article) { this.addArticleToMap(this.boughtArticles, user, article);}
    public void addBoughtArticle (UUID id, Article article) {
        this.addArticleToMap(this.boughtArticles, id, article);
    }

    private void addArticleToMap (Map<UUID, ArrayList<Article>> map, User user, Article article) {
        if (!map.containsKey(user.getId())) {
            ArrayList<Article> newArticleList = new ArrayList<>();
            newArticleList.add(article);
            map.put(user.getId(), newArticleList);
        } else {
            map.get(user.getId()).add(article);
        }
    }

    private void addArticleToMap (Map<UUID, ArrayList<Article>> map, UUID id, Article article) {
        if (!map.containsKey(id)) {
            ArrayList<Article> newArticleList = new ArrayList<>();
            newArticleList.add(article);
            map.put(id, newArticleList);
        } else {
            map.get(id).add(article);
        }
    }

    public void addArticleToCart(User buyer, Article article, LocalDateTime now) {
        addArticleToCart(buyer.getId(), article, now);
    }

    public void addArticleToCart(UUID buyerID, Article article, LocalDateTime now) {
        if (this.userCart.containsKey(buyerID)) {
            Order order = this.userCart.get(buyerID);
            order.addArticle(article);
        } else {
            Order newOrder = new Order(buyerID, now);
            newOrder.addArticle(article);
            this.userCart.put(buyerID, newOrder);
        }
    }

    private Map<UUID, ArrayList<Article>> mapDeepCopy (Map<UUID, ArrayList<Article>> articles) {
        Map<UUID, ArrayList<Article>> newMap = new HashMap<>();
        for (Map.Entry<UUID, ArrayList<Article>> entry : articles.entrySet()) {
            UUID id = entry.getKey();
            ArrayList<Article> originalList = entry.getValue();
            ArrayList<Article> copiedList = new ArrayList<>();
            for (Article article : originalList) {
                copiedList.add(article.clone());
            }
            newMap.put(id, copiedList);
        }
        return newMap;
    }

    public ArrayList<ArticleInfo> getUserForSaleArticlesInfo (User user) {
        ArrayList<ArticleInfo> info = new ArrayList<>();
        if (this.forSaleArticles.containsKey(user.getId())) {
            for (Article article : this.forSaleArticles.get(user.getId())) {
                ArticleInfo articleInfo = new ArticleInfo(article.getSellerId(), article.getId(), article.toString());
                info.add(articleInfo);
            }
        }
        return info;
    }

    public ArrayList<ArticleInfo> getUserSoldArticlesInfo (User user) {
        ArrayList<ArticleInfo> info = new ArrayList<>();
        if (this.soldArticles.containsKey(user.getId())) {
            for (Article article : this.soldArticles.get(user.getId())) {
                ArticleInfo articleInfo = new ArticleInfo(article.getSellerId(), article.getId(), article.toString());
                info.add(articleInfo);
            }
        }
        return info;
    }

    public ArrayList<ArticleInfo> getNotInCartForSaleArticlesInfo (User buyer) {
        ArrayList<ArticleInfo> info = new ArrayList<>();
        for (ArrayList<Article> articles : this.forSaleArticles.values()) {
            for (Article article : articles) {
                if (!article.getSellerId().equals(buyer.getId())) {
                    if (!(this.userCart.containsKey(buyer.getId()) && this.userCart.get(buyer.getId()).hasArticle(article))) {
                        ArticleInfo articleInfo = new ArticleInfo(article.getSellerId(), article.getId(), article.toString());
                        info.add(articleInfo);
                    }
                }
            }
        }
        return info;
    }

    public ArrayList<ArticleInfo> getArticlesInCartInfo (User buyer) {
        if (this.userCart.containsKey(buyer.getId())) {
            return this.userCart.get(buyer.getId()).getArticlesInfo();
        } else {
            return null;
        }
    }

    public void removeForSaleArticle (User user, Article article) {
        if (this.forSaleArticles.containsKey(user.getId())) {
            this.forSaleArticles.get(user.getId()).remove(article);
            this.removeArticleFromCarts(article);
        }
    }

    public void removeForSaleArticle (UUID userID, Article article) {
        if (this.forSaleArticles.containsKey(userID)) {
            this.forSaleArticles.get(userID).remove(article);
            this.removeArticleFromCarts(article);
        }
    }

    private void removeArticleFromCarts (Article article) {
        for (Order order : this.userCart.values()) {
            order.removeArticle(article);
        }
    }

    public void removeArticleFromCart (User user, Article article) {
        if (this.userCart.containsKey(user.getId())) {
            this.userCart.get(user.getId()).removeArticle(article);
        }
    }

    public Order buyOrder (User user) {
        if (this.userCart.containsKey(user.getId())) {
            Order order = this.userCart.get(user.getId());

            ArrayList<Article> cartArticles = order.getArticles();

            this.userCart.remove(user.getId());
            for (Article article : cartArticles) {
                this.removeForSaleArticle(article.getSellerId(), article); // removes the articles from all carts (including the order that is done)
                this.addBoughtArticle(user.getId(), article);
                this.addSoldArticle(article.getSellerId(), article);
            }

            Order clone = order.clone();
            this.addUserOrder(user, clone);
            return clone;
        }
        return null;
    }

    public void addUserOrder (User user, Order order) {
        if (!this.userOrders.containsKey(user.getId())) {
            ArrayList<Order> newOrderList = new ArrayList<>();
            newOrderList.add(order);
            this.userOrders.put(user.getId(), newOrderList);
        } else {
            this.userOrders.get(user.getId()).add(order);
        }
    }

    public double getCartPrice (UUID userID) {
        if (this.userCart.containsKey(userID)) {
            Order cart = this.userCart.get(userID);
            return cart.getFinalPrice();
        }
        return 0;
    }

    public double getCartPrice (User user) {
        return this.getCartPrice(user.getId());
    }

    public UUID getOrderID (UUID userID, int n) {
        if (this.userOrders.containsKey(userID)) {
            ArrayList<Order> orders = this.userOrders.get(userID);
            orders.sort(Comparator.comparing(Order::getCreationDate));
            return orders.get(n-1).getId();
        }
        return null;
    }

    public ArrayList<OrderInfo> getOrdersInfo(User user) {
        ArrayList<OrderInfo> info = new ArrayList<>();
        if (this.userOrders.containsKey(user.getId())) {
            for (Order order : this.userOrders.get(user.getId())) {
                OrderInfo orderInfo = new OrderInfo(order.getId(), order.toString(), order.notSent(), order.hasArrived());
                info.add(orderInfo);
            }
        }
        return info;
    }

    public void canceledOrder (Order order) {
        if (this.userOrders.containsKey(order.getBuyerId())) {
            ArrayList<Order> orders = this.userOrders.get(order.getBuyerId());
            Iterator<Order> it = orders.iterator();
            while (it.hasNext()) {
                Order el = it.next();
                if (el.equals(order)) {
                    it.remove();
                    order.removePackages();
                }
            }

            reorganizeOrderArticles(order);
        }
    }

    public void returnedOrder (Order order) {
        if (this.userOrders.containsKey(order.getBuyerId())) {
            reorganizeOrderArticles(order);
        }
    }

    private void reorganizeOrderArticles(Order order) {
        ArrayList<Article> articles = order.getArticles();
        for (Article article : articles) {
            if (this.boughtArticles.containsKey(order.getBuyerId()))  {this.boughtArticles.get(order.getBuyerId()).remove(article);}
            if (this.soldArticles.containsKey(article.getSellerId())) {this.soldArticles.get(article.getSellerId()).remove(article);}
            this.addForSaleArticle(article.getSellerId(), article);
        }
    }

    @Override
    public UserManager clone() {
        return new UserManager(this);
    }

}
