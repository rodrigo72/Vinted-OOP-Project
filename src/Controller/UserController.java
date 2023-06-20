package Controller;

import Logs.AppLogger;
import Model.AppModel;
import Model.Data.Exceptions.NotFoundInCatalogException;
import Model.Factories.ArticleFactory;
import Model.Types.Articles.Article;
import Model.Types.Articles.ArticleInfo;
import Model.Types.Articles.TShirt;
import Model.Types.Carriers.Carrier;
import Model.Types.Exceptions.ArticleNegativeNumberException;
import Model.Types.Exceptions.OrderNotFoundException;
import Model.Types.Exceptions.ReturnOrderException;
import Model.Types.Exceptions.UserNotFoundException;
import Model.Types.OrderInfo;
import Model.Types.User;
import View.AppView;
import View.UserView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class UserController {
    private User user;
    private Scanner scanner;
    private AppModel appModel;
    private UserView userView;
    private ArticleFactory articleFactory;
    private int option;

    public UserController (Scanner scanner, AppModel model, AppView appView) {
        this.option = -1;
        this.scanner = scanner;
        this.appModel = model;
        this.userView = new UserView(appView);
        this.articleFactory = new ArticleFactory();
    }

    public void setUser (User user) {
        this.user = user;
    }

    public void updateLanguage() {
        this.userView.updateLanguage();
    }

    // user mode menu
    public void run() {
        do {
            try {
                this.userView.userMode();
                this.option = this.scanner.nextInt();

                switch (this.option) {
                    case 1 -> { // sell article
                        try { this.sell(); }
                        catch (NotFoundInCatalogException e) {
                            AppLogger.logError(e);
                        }
                    }
                    case 2 -> { // view available articles to buy
                        try {
                            this.addToCart();
                        } catch (ArticleNegativeNumberException e) {
                            AppLogger.logError(e);
                        }
                    }
                    case 3 -> { // view user's cart
                        try {
                            this.displayCart();
                        } catch (ArticleNegativeNumberException e) {
                            AppLogger.logError(e);
                        }
                    }
                    case 4 -> // view user's sold articles
                            this.displayUserSoldArticles();
                    case 5 -> // view user's for sale articles
                            this.displayUserForSaleArticles();
                    case 6 -> // view orders
                            this.displayUserOrders();
                    case 7 -> this.timeMachine();
                }
            }
            catch (InputMismatchException e) {
                this.userView.errorInput();
                AppLogger.logError(e);
                this.scanner.nextLine();
            }
        } while (this.option != 0);
    }

    public void sell() throws NotFoundInCatalogException {
        int optionBehaviour;
        do {
            this.userView.articleBehaviour();
            optionBehaviour = this.scanner.nextInt();
        } while (optionBehaviour <= 0 || optionBehaviour > 4);

        int numberOfOwners = 0;
        double stateOfUse = 0, markup = 0;
        UUID sellerID = this.user.getId();

        if (optionBehaviour == 1 || optionBehaviour == 3) { // used
            this.userView.articleNumberOfOwners();
            numberOfOwners = this.scanner.nextInt();
            this.userView.articleStateOfUse();
            stateOfUse = this.scanner.nextDouble();
        }

        if (optionBehaviour == 2 || optionBehaviour == 3) { // premium
            this.userView.articleMarkup();
            markup = this.scanner.nextDouble();
        }

        this.scanner.nextLine();
        Carrier carrier;
        int number;
        List<String> namesList = this.appModel.getCarrierCatalogNamesList();

        if (namesList.size() == 0) {
            this.userView.errorCarrier();
            return;
        }

        do {
            this.userView.printCarrierNamesList(namesList);
            number = this.scanner.nextInt();
        } while (number <= 0 || number > namesList.size());

        carrier = this.appModel.getCarrier(namesList.get(number-1));

        this.userView.typeOfArticle();
        int optionType = this.scanner.nextInt();
        this.scanner.nextLine();

        this.userView.articleDescription();
        String description = this.scanner.nextLine();
        this.userView.articleBrand();
        String brand = this.scanner.nextLine();
        this.userView.articleBasePrice();
        double basePrice = this.scanner.nextDouble();
        this.scanner.nextLine();

        switch (optionType) {
            case 1 -> { // TShirt
                boolean loop = true;
                TShirt.Size sizeEnum = null;
                TShirt.Pattern patternEnum = null;

                do {
                    this.userView.articleShirtSize();
                    String size = this.scanner.nextLine();

                    switch (size) {
                        case "S", "s", "1" -> {
                            sizeEnum = TShirt.Size.S;
                            loop = false;
                        }
                        case "M", "m", "2" -> {
                            sizeEnum = TShirt.Size.M;
                            loop = false;
                        }
                        case "L", "l", "3" -> {
                            sizeEnum = TShirt.Size.L;
                            loop = false;
                        }
                        case "XL", "xl", "4" -> {
                            sizeEnum = TShirt.Size.XL;
                            loop = false;
                        }
                    }
                } while (loop);

                loop = true;
                do {
                    this.userView.articleShirtPatter();
                    int pattern = this.scanner.nextInt();

                    if (pattern == 1) {
                        patternEnum = TShirt.Pattern.SOLID;
                        loop = false;
                    } else if (pattern == 2) {
                        patternEnum = TShirt.Pattern.STRIPES;
                        loop = false;
                    } else if (pattern == 3) {
                        patternEnum = TShirt.Pattern.PALM_TREES;
                        loop = false;
                    }
                } while (loop);

                Article article;
                if (optionBehaviour == 1 || optionBehaviour == 3) { // (used) or (used and premium)
                    article = this.articleFactory.createTShirt(description, brand, basePrice, numberOfOwners, stateOfUse, carrier, sellerID, sizeEnum, patternEnum);
                } else { // (premium) or (none)
                    article = this.articleFactory.createTShirt(description, brand, basePrice, carrier, sellerID, sizeEnum, patternEnum);
                }

                this.appModel.sellArticle(this.user, article);
            }
            case 2 -> { // Bag
                this.userView.articleBagHeight();
                int height = this.scanner.nextInt();
                this.userView.articleBagWidth();
                int width = this.scanner.nextInt();
                this.userView.articleBagLength();
                int length = this.scanner.nextInt();
                this.scanner.nextLine();
                this.userView.articleBagMaterial();
                String material = this.scanner.nextLine();
                this.userView.articleBagCollectionYear();
                int collectionYear = this.scanner.nextInt();
                this.userView.articleBagDiscount();
                double discount = this.scanner.nextDouble();

                int[] dimensions = {height, width, length};

                Article article;
                if (optionBehaviour == 1) { // used
                    article = this.articleFactory.createBag(description, brand, basePrice, numberOfOwners, stateOfUse, carrier, sellerID, dimensions, material, collectionYear, discount);
                } else if (optionBehaviour == 2) { // premium
                    article = this.articleFactory.createBag(description, brand, basePrice, markup, carrier, sellerID, dimensions, material, collectionYear, discount);
                } else if (optionBehaviour == 3) { // used and premium
                    article = this.articleFactory.createBag(description, brand, basePrice, numberOfOwners, stateOfUse, markup, carrier, sellerID, dimensions, material, collectionYear, discount);
                } else { // none
                    article = this.articleFactory.createBag(description, brand, basePrice, carrier, sellerID, dimensions, material, collectionYear, discount);
                }

                this.appModel.sellArticle(this.user, article);
            }
            case 3 -> { // Shoes
                this.userView.articleShoesSize();
                int size = this.scanner.nextInt();
                this.scanner.nextLine();

                boolean loop = true, hasLacesBoolean = false;
                do {
                    this.userView.articleShoesHasLaces();
                    String hasLaces = this.scanner.nextLine();

                    if (hasLaces.equals("y") || hasLaces.equals("Y") || hasLaces.equals("1")) {
                        hasLacesBoolean = true;
                        loop = false;
                    } else if (hasLaces.equals("n") || hasLaces.equals("N") || hasLaces.equals("2")) {
                        loop = false;
                    }
                } while (loop);

                this.userView.articleShoesColor();
                String color = this.scanner.nextLine();
                this.userView.articleShoesCollectionYear();
                int collectionYear = this.scanner.nextInt();
                this.userView.articleShoesDiscount();
                double discount = this.scanner.nextDouble();

                Article article;
                if (optionBehaviour == 1) { // used
                    article = this.articleFactory.createShoes(description, brand, basePrice, numberOfOwners, stateOfUse, carrier, sellerID, size, hasLacesBoolean, color, collectionYear, discount);
                } else if (optionBehaviour == 2) { // premium
                    article = this.articleFactory.createShoes(description, brand, basePrice, markup, carrier, sellerID, size, hasLacesBoolean, color, collectionYear, discount);
                } else if (optionBehaviour == 3) { // used and premium
                    article = this.articleFactory.createShoes(description, brand, basePrice, numberOfOwners, stateOfUse, markup, carrier, sellerID, size, hasLacesBoolean, color, collectionYear, discount);
                } else { // none
                    article = this.articleFactory.createShoes(description, brand, basePrice, carrier, sellerID, size, hasLacesBoolean, color, collectionYear, discount);
                }

                this.appModel.sellArticle(this.user, article);
            }
        }
    }

    public void addToCart() throws ArticleNegativeNumberException {
        ArrayList<ArticleInfo> info = this.appModel.getNotInCartForSaleArticlesInfo(this.user);
        int choice;

        if (info.size() == 0) {
            this.userView.errorArticle();
            return;
        }

        ArrayList<String> articlesAsStrings = new ArrayList<>();
        for (ArticleInfo elem : info) articlesAsStrings.add(elem.display());
        int size = articlesAsStrings.size();
        boolean[] added = new boolean[size];
        Arrays.fill(added, false);

        this.userView.cartAdd();
        this.userView.displayList(articlesAsStrings);
        do {
            choice = this.scanner.nextInt();
            if (choice > 0 && choice <= articlesAsStrings.size() && !added[choice-1]) {
                ArticleInfo chosen = info.get(choice-1);
                this.appModel.addArticleToCart(this.user, chosen.articleID());
                added[choice-1] = true;
                size--;
                this.userView.forSaleArticlesAdded();
            }
        } while (choice != 0 && size != 0);

    }

    public void displayCart() throws ArticleNegativeNumberException {
        ArrayList<ArticleInfo> info = this.appModel.getArticlesInCartInfo(this.user);
        int choice;

        if (info == null || info.size() == 0) {
            this.userView.errorArticle();
            return;
        }

        ArrayList<String> articlesAsStrings = new ArrayList<>();

        for (ArticleInfo elem : info) articlesAsStrings.add(elem.display());
        int size = articlesAsStrings.size();
        boolean[] removed = new boolean[size];
        Arrays.fill(removed, false);

        this.userView.cartView();
        this.userView.displayList(articlesAsStrings);
        this.userView.cartOptions();
        this.userView.displayTotalPrice("Order", this.appModel.getCartPrice(this.user));

        boolean repeat = false;
        do {
            choice = this.scanner.nextInt();

            if (choice == 1) { // remove from cart
                int number;
                this.userView.cartNumberToRemove();
                do {
                    number = this.scanner.nextInt();
                    if (number > 0 && number <= articlesAsStrings.size() && !removed[number-1]) {
                            ArticleInfo chosen = info.get(number-1);
                            this.appModel.removeArticleFromCart(this.user, chosen.articleID());
                            this.userView.cartRemovedArticle();
                            this.userView.displayTotalPrice("Order", this.appModel.getCartPrice(this.user));
                            removed[number-1] = true;
                            size--;
                        }
                    } while(number != 0 && size != 0);
            } else if (choice == 2) { // buy order
                try {
                    this.appModel.buyOrder(this.user);
                }
                catch (UserNotFoundException e) {
                    AppLogger.logError(e);
                    this.userView.errorUserNotFount();
                }
                catch (Exception e) {
                    this.userView.error();
                    AppLogger.logError(e);
                }
            } else if (choice != 0) { // wrong option
                repeat = true;
            }

        } while (repeat && choice != 0);
    }

    public void displayUserForSaleArticles() {
        ArrayList<ArticleInfo> info = this.appModel.getUserForSaleArticlesInfo(this.user);

        if (info == null || info.size() == 0) {
            this.userView.errorArticle();
            return;
        }

        ArrayList<String> articlesAsStrings = new ArrayList<>();
        for (ArticleInfo elem : info) articlesAsStrings.add(elem.display());
        int size = articlesAsStrings.size();
        boolean[] removed = new boolean[size];
        Arrays.fill(removed, false);

        this.userView.forSaleArticlesView();
        this.userView.displayList(articlesAsStrings);
        this.userView.forSaleArticlesRemove();
        int number;
        do {
            number = this.scanner.nextInt();
            if (number > 0 && number <= articlesAsStrings.size() && !removed[number-1]) {
                ArticleInfo chosen = info.get(number-1);
                this.appModel.removeForSaleArticle(this.user, chosen.articleID());
                this.userView.forSaleArticlesRemoved();
                removed[number-1] = true;
                size--;
            }
        } while (number != 0 && size != 0);
    }

    public void displayUserSoldArticles() {
        ArrayList<ArticleInfo> info = this.appModel.getUserSoldArticlesInfo(this.user);

        if (info == null || info.size() == 0) {
            this.userView.errorArticle();
            return;
        }

        ArrayList<String> articlesAsStrings = new ArrayList<>();
        for (ArticleInfo elem : info) articlesAsStrings.add(elem.display());

        this.userView.soldArticlesView();
        this.userView.displayList(articlesAsStrings);
        this.scanner.nextInt();
    }

    public void displayUserOrders() {
        ArrayList<OrderInfo> info = this.appModel.getOrdersInfo(this.user);

        if (info == null || info.size() == 0) {
            this.userView.errorArticle();
            return;
        }

        ArrayList<String> ordersAsStrings = new ArrayList<>();
        for (OrderInfo elem : info) ordersAsStrings.add(elem.orderAsString());

        int choice;
        do {
            this.userView.ordersView();
            this.userView.displayList(ordersAsStrings);
            this.userView.orderSelect();
            choice = this.scanner.nextInt();

            if (choice > 0 && choice <= ordersAsStrings.size()) {
                OrderInfo orderInfo = info.get(choice-1);

                if (orderInfo.arrived()) {
                    this.userView.orderReturn();

                    int choice2 = this.scanner.nextInt();
                    if (choice2 == 1) {
                        try {
                            this.appModel.returnOrder(this.user, orderInfo.orderID());
                        }
                        catch (ReturnOrderException e) {
                            AppLogger.logError(e);
                            this.userView.cannotReturnOrder();
                        }
                        catch (UserNotFoundException e) {
                            AppLogger.logError(e);
                            this.userView.errorUserNotFount();
                        }
                        catch (OrderNotFoundException e) {
                            AppLogger.logError(e);
                            this.userView.error();
                        }
                    }
                } else if (orderInfo.notSent()) {
                    this.userView.orderCancel();

                    int choice2 = this.scanner.nextInt();
                    if (choice2 == 1) {
                        try {
                            this.appModel.cancelOrder(this.user, orderInfo.orderID());
                        }
                        catch (UserNotFoundException e) {
                            AppLogger.logError(e);
                            this.userView.errorUserNotFount();
                        }
                        catch (OrderNotFoundException e) {
                            AppLogger.logError(e);
                            this.userView.error();
                        }
                    }
                } else {
                    this.userView.cannotCancelOrReturn();
                }
                choice = 0;
            }

        } while (choice != 0);
    }

    // advance to a specific date
    public void timeMachine() {

        this.scanner.nextLine();
        this.userView.timeMachineDate();
        String dateInput = this.scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date;
        try {
            date = LocalDate.parse(dateInput, formatter);
        } catch (DateTimeParseException e) {
            this.userView.errorInput();
            return;
        }

        try {
            this.appModel.timeMachine(date);
        } catch (ArticleNegativeNumberException e) {
            AppLogger.logError(e);
        }
    }
}