package Model;

import Model.Data.Catalogs.ArticleCatalog;
import Model.Data.Catalogs.CarrierCatalog;
import Model.Data.Catalogs.OrderCatalog;
import Model.Data.Catalogs.UserCatalog;
import Model.Data.Exceptions.AlreadyExistsInCatalogException;
import Model.Data.Exceptions.NotFoundInCatalogException;
import Model.Data.UserManager;
import Model.Types.Articles.Article;
import Model.Types.Articles.ArticleInfo;
import Model.Types.Carriers.Carrier;
import Model.Types.Exceptions.*;
import Model.Types.Order;
import Model.Types.OrderInfo;
import Model.Types.User;

import java.io.Serializable;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class AppModel implements Serializable {
    private final AppClock clock;
    private final ArticleCatalog articleCatalog;
    private final UserCatalog userCatalog;
    private final CarrierCatalog carrierCatalog;
    private final OrderCatalog orderCatalog;
    private final UserManager userManager;
    private double vintageMoneyEarned;

    public AppModel() {
        this.clock = AppClock.of(Instant.now(), ZoneId.systemDefault());
        this.articleCatalog = new ArticleCatalog();
        this.userCatalog = new UserCatalog();
        this.carrierCatalog = new CarrierCatalog();
        this.orderCatalog = new OrderCatalog();
        this.userManager = new UserManager();
    }

    public AppModel(AppModel model) {
        this.articleCatalog = model.getArticleCatalog();
        this.userCatalog = model.getUserCatalog();
        this.carrierCatalog = model.getCarrierCatalog();
        this.orderCatalog = model.getOrderCatalog();
        this.userManager = model.getUserArticlesManager();
        this.clock = model.getAppClock();
    }

    public void addUserToCatalog (String name, String email, String country, String city, String street, String postalCode) throws AlreadyExistsInCatalogException {
        this.userCatalog.add(name, email, country, city, street, postalCode);
    }

    public void addCarrierToCatalog (String name, double basePriceSmall, double basePriceMedium, double basePriceBig, double tax, double profitRate) throws AlreadyExistsInCatalogException {
        this.carrierCatalog.add(name, basePriceSmall, basePriceMedium, basePriceBig, tax, profitRate);
    }

    public void addPremiumCarrierToCatalog (String name, double basePriceSmall, double basePriceMedium, double basePriceBig, double tax, double profitRate, double additionalPrice) throws AlreadyExistsInCatalogException {
        this.carrierCatalog.add(name, basePriceSmall, basePriceMedium, basePriceBig, tax, profitRate, additionalPrice);
    }

    public void addArticleToCart (User buyer, UUID articleID) {
        Article article = this.articleCatalog.get(articleID);
        this.userManager.addArticleToCart(buyer, article, this.clock.getLocalDateTime());
    }

    public void addArticleToCart (UUID buyerID, UUID articleID) {
        Article article = this.articleCatalog.get(articleID);
        this.userManager.addArticleToCart(buyerID, article, this.clock.getLocalDateTime());
    }

    public void addArticleToCatalog (Article article) {
        this.articleCatalog.add(article);
    }
    public boolean login (String email) {
        return this.userCatalog.exists(email);
    }
    public User getUser (String email) {
        return this.userCatalog.get(email);
    }
    public UUID getUserID (String email) throws UserNotFoundException {
        if (this.userCatalog.exists(email)) {
            User user = this.userCatalog.get(email);
            return user.getId();
        } else {
            throw new UserNotFoundException();
        }
    }

    public Carrier getCarrier (String name) throws NotFoundInCatalogException {
        if (this.carrierCatalog.exists(name)) {
            return this.carrierCatalog.get(name);
        } else {
            throw new NotFoundInCatalogException();
        }
    }

    public double getVintageMoneyEarned() {
        return vintageMoneyEarned;
    }

    public List<String> getCarrierCatalogNamesList() {
        return this.carrierCatalog.getNamesList();
    }

    public void sellArticle (User seller, Article article) {
        this.addArticleToCatalog(article);
        article.updatePriceCorrection(this.clock.getYear());
        this.userManager.addForSaleArticle(seller, article);
    }

    public void sellArticle (UUID seller, Article article) {
        article.updatePriceCorrection(this.clock.getYear());
        this.addArticleToCatalog(article);
        this.userManager.addForSaleArticle(seller, article);
    }

    public ArrayList<ArticleInfo> getNotInCartForSaleArticlesInfo(User buyer) throws ArticleNegativeNumberException {
        this.updateArticleCatalog();
        return this.userManager.getNotInCartForSaleArticlesInfo(buyer);
    }

    public ArrayList<ArticleInfo> getArticlesInCartInfo (User buyer) throws ArticleNegativeNumberException {
        this.updateArticleCatalog();
        return this.userManager.getArticlesInCartInfo(buyer);
    }

    public ArrayList<ArticleInfo> getUserForSaleArticlesInfo (User user) {
        return this.userManager.getUserForSaleArticlesInfo(user);
    }

    public ArrayList<ArticleInfo> getUserSoldArticlesInfo (User user) {
        return this.userManager.getUserSoldArticlesInfo(user);
    }

    public void updateArticleCatalog() throws ArticleNegativeNumberException {
        int currentYear = this.clock.getYear();
        this.articleCatalog.notifyArticles(currentYear);
    }

    public void removeForSaleArticle (User user, UUID articleID) {
        Article article = this.articleCatalog.get(articleID);
        this.userManager.removeForSaleArticle(user, article);
        this.articleCatalog.remove(article);
    }

    public void removeArticleFromCart (User user, UUID articleID) {
        Article article = this.articleCatalog.get(articleID);
        this.userManager.removeArticleFromCart(user, article);
    }

    public double getCartPrice (User user) {
        return this.userManager.getCartPrice(user);
    }

    public double getCartPrice (UUID userID) {
        return this.userManager.getCartPrice(userID);
    }

    public UUID getOrderID (UUID userID, int n) {
        return this.userManager.getOrderID(userID, n);
    }

    public void buyOrder (UUID userID) throws UserNotFoundException {
        User user = this.userCatalog.get(userID);
        this.buyOrder(user);
    }

    public void buyOrder (User user) throws UserNotFoundException { // error handling here
        Order order = this.userManager.buyOrder(user);
        if (order != null) {
            this.orderCatalog.add(order);
            order.setAsFinalized(this.clock.getLocalDateTime());
            user.addToMoneySpent(order.getFinalPrice(), this.clock.getLocalDate());
            for (Article article : order.getArticles()) {
                if (this.userCatalog.exists(article.getSellerId())) {
                    this.userCatalog.get(article.getSellerId()).addToMoneyEarned(article.getPriceCorrection(), this.clock.getLocalDate());
                } else {
                    throw new UserNotFoundException();
                }
            }
            this.vintageMoneyEarned += order.getVintageProfit();
            order.setCarriersProfit();
        } else {
            throw new NullPointerException();
        }
    }

    public void timeMachine (LocalDate date) throws ArticleNegativeNumberException {
        this.updateOrders();
        while (this.clock.isDateAfter(date)) {
            int year = this.clock.getYear();
            this.clock.add(Duration.ofDays(1));
            if (this.clock.getYear() != year) {
                this.updateArticleCatalog();
            }
            this.updateOrders();
        }
    }

    public void setDate (Date date) {
        this.clock.setDate(date);
    }

    public void cancelOrder (UUID userID, UUID orderID) throws UserNotFoundException, OrderNotFoundException {
        if (this.userCatalog.exists(userID)) {
            User user = this.userCatalog.get(userID);
            this.cancelOrder(user, orderID);
        } else {
            throw new UserNotFoundException();
        }
    }

    public void cancelOrder (User user, UUID orderID) throws UserNotFoundException, OrderNotFoundException {
        if (this.orderCatalog.exists(orderID)) {
            Order order = this.orderCatalog.get(orderID);
            if (order.isCancelable()) {
                this.orderCatalog.remove(orderID);
                this.userManager.canceledOrder(order);
                this.returnMoney(user, order);
            }
        } else {
            throw new OrderNotFoundException();
        }
    }

    public void returnOrder (UUID userID, UUID orderID) throws UserNotFoundException, OrderNotFoundException, ReturnOrderException {
        if (this.userCatalog.exists(userID)) {
            User user = this.userCatalog.get(userID);
            this.returnOrder(user, orderID);
        } else {
            throw new UserNotFoundException();
        }
    }

    public void returnOrder (User user, UUID orderID) throws ReturnOrderException, OrderNotFoundException, UserNotFoundException {
        if (this.orderCatalog.exists(orderID)) {
            Order order = this.orderCatalog.get(orderID);
            if (order.isReturnable(this.clock.getLocalDateTime())) {
                order.setAsReturned(this.clock.getLocalDateTime());
                this.userManager.returnedOrder(order);
                this.returnMoney(user, order);
            } else {
                throw new ReturnOrderException();
            }
        } else {
            throw new OrderNotFoundException();
        }
    }

    private void returnMoney(User user, Order order) throws UserNotFoundException {
        for (Article article : order.getArticles()) {
            if (this.userCatalog.exists(article.getSellerId())) {
                user.removeFromMoneySpent(article.getPriceCorrection(), order.getFinalizedDate().toLocalDate());
                this.userCatalog.get(article.getSellerId()).removeFromMoneyEarned(article.getPriceCorrection(), order.getFinalizedDate().toLocalDate());
            } else {
                throw new UserNotFoundException();
            }
        }
    }

    public String getUserWhoEarnedTheMost() throws UserNotFoundException {
        User user = this.userCatalog.getHighestEarnerAllTime();
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user.toString();
    }

    public String getUserWhoEarnedTheMostInPeriodOfTime (LocalDate infDate, LocalDate supDate) throws UserNotFoundException {
        User user = this.userCatalog.getHighestEarnerPeriodOfTime(infDate, supDate);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user.toString();
    }

    public String getUserWhoSpentTheMost() throws UserNotFoundException {
        User user = this.userCatalog.getHighestSpenderAllTime();
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user.toString();
    }

    public String getUserWhoSpentTheMostInPeriodOfTime (LocalDate infDate, LocalDate supDate) throws UserNotFoundException {
        User user = this.userCatalog.getHighestSpenderPeriodOfTime(infDate, supDate);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user.toString();
    }

    public String getCarrierThatEarnedTheMost() throws CarrierNotFoundException {
        Carrier carrier = this.carrierCatalog.getRichestCarrier();
        if (carrier == null) {
            throw new CarrierNotFoundException();
        }
        return carrier.toString();
    }

    public ArrayList<String> sortedHighestEarningInPeriodOfTimeAsStrings (LocalDate infDate, LocalDate supDate) {
        return this.userCatalog.sortedHighestEarningInPeriodOfTimeAsStrings(infDate, supDate);
    }

    public ArrayList<String> sortedHighestSpendingInPeriodOfTimeAsStrings (LocalDate infDate, LocalDate supDate) {
        return this.userCatalog.sortedHighestSpendingInPeriodOfTimeAsStrings(infDate, supDate);
    }

    public double getArticlePrice (UUID articleID) throws NotFoundInCatalogException {
        return this.articleCatalog.get(articleID).getPriceCorrection();
    }

    public UUID getArticle (String description) throws NotFoundInCatalogException {
        return this.articleCatalog.get(description);
    }

    public void changeNameOfUser (UUID userID, String name) throws UserNotFoundException {
        Consumer<User> changeName = user -> user.setName(name);
        this.changeUser(userID, changeName);
    }

    public void changeCountryOfUser (UUID userID, String country) throws UserNotFoundException {
        Consumer<User> changeCountry = user -> user.setCountry(country);
        this.changeUser(userID, changeCountry);
    }

    public void changeCityOfUser (UUID userID, String city) throws UserNotFoundException {
        Consumer<User> changeCity = user -> user.setCity(city);
        this.changeUser(userID, changeCity);
    }

    public void changeStreetOfUser (UUID userID, String street) throws UserNotFoundException {
        Consumer<User> changeStreet = user -> user.setStreet(street);
        this.changeUser(userID, changeStreet);
    }

    public void changePostalCodeOfUser (UUID userID, String postalCode) throws UserNotFoundException {
        Consumer<User> changePostalCode = user -> user.setPostalCode(postalCode);
        this.changeUser(userID, changePostalCode);
    }

    public void changeUser(UUID userID, Consumer<User> changeFunction) throws UserNotFoundException {
        if (this.userCatalog.exists(userID)) {
            User user = this.userCatalog.get(userID);
            changeFunction.accept(user);
        } else {
            throw new UserNotFoundException();
        }
    }

    public void updateOrders() {
        this.orderCatalog.notifyAllOrders(this.clock.getLocalDateTime());
    }

    public ArrayList<OrderInfo> getOrdersInfo(User user) {
        return this.userManager.getOrdersInfo(user);
    }

    public ArrayList<String> getAllArticlesAsString() {
        return this.articleCatalog.getValuesAsStrings();
    }

    public ArrayList<String> getAllUsersAsString() {
        return this.userCatalog.getValuesAsStrings();
    }

    public ArrayList<String> getAllCarriersAsString() {
        return this.carrierCatalog.getValuesAsStrings();
    }

    public ArrayList<String> getAllOrdersAsString() {
        return this.orderCatalog.getValuesAsStrings();
    }

    public ArticleCatalog getArticleCatalog() {
        return articleCatalog;
    }
    public UserCatalog getUserCatalog() {
        return userCatalog;
    }
    public CarrierCatalog getCarrierCatalog() {
        return carrierCatalog;
    }
    public OrderCatalog getOrderCatalog() {
        return orderCatalog;
    }
    public UserManager getUserArticlesManager() {
        return userManager;
    }
    public AppClock getAppClock() {
        return this.clock;
    }
}