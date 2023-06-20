package Controller;

import Logs.AppLogger;
import Model.AppModel;
import Model.Data.Exceptions.AlreadyExistsInCatalogException;
import Model.Data.Exceptions.NotFoundInCatalogException;
import Model.Factories.ArticleFactory;
import Model.State;
import Model.Types.Articles.Article;
import Model.Types.Articles.TShirt;
import Model.Types.Carriers.Carrier;
import Model.Types.Exceptions.ArticleNegativeNumberException;
import Model.Types.Exceptions.OrderNotFoundException;
import Model.Types.Exceptions.ReturnOrderException;
import Model.Types.Exceptions.UserNotFoundException;
import Model.Types.User;
import View.AppView;
import View.UserView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Parser {
    private AppModel appModel;
    private AppView appView;
    private ArticleFactory articleFactory;
    private State state;

    public Parser (AppModel appModel, State state, AppView appView) {
        this.appModel = appModel;
        this.appView = appView;
        this.state = state;
        this.articleFactory = new ArticleFactory();
    }

    public void parseUserFile (String fileName) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("src/Resources/CSV Files/" + fileName), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] fields = line.split(","); // split the line into fields
                if (fields.length == 6) { // check that there are exactly 6 fields
                    this.appModel.addUserToCatalog(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
                }
            }
        }
        catch (IOException | AlreadyExistsInCatalogException e) {
            AppLogger.logError(e);
        }
    }

    public void parseCarrierFile (String fileName) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("src/Resources/CSV Files/" + fileName), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] fields = line.split(","); // split the line into fields
                this.createCarrier(fields);
            }
        }
        catch (IOException | AlreadyExistsInCatalogException e) {
            AppLogger.logError(e);
        }
    }

    public void parseArticleFile (String fileName) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("src/Resources/CSV Files/" + fileName), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] fields = line.split(","); // split the line into fields
                this.sellArticle(fields, 0);
            }
        }
        catch (IOException | UserNotFoundException | NotFoundInCatalogException e) {
            AppLogger.logError(e);
        }
    }

    private void createCarrier (String[] fields) throws AlreadyExistsInCatalogException {
        if (fields.length == 6) { // check that there are exactly 6 fields
            this.appModel.addCarrierToCatalog(fields[0], Double.parseDouble(fields[1]), Double.parseDouble(fields[2]), Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), Double.parseDouble(fields[5]));
        }
        else if (fields.length == 7) {
            this.appModel.addPremiumCarrierToCatalog(fields[0], Double.parseDouble(fields[1]), Double.parseDouble(fields[2]), Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), Double.parseDouble(fields[5]), Double.parseDouble(fields[6]));
        }
    }

    private void sellArticle (String[] line, int i) throws UserNotFoundException, NotFoundInCatalogException {
        if (line.length > 10 && line.length < 22) {
            UUID sellerID = this.appModel.getUserID(line[i]);
            Carrier carrier = this.appModel.getCarrier(line[i+1]);

            enum Type {bag, tShirt, shoes, other}
            Type type = Type.other;
            boolean used = false, premium = false;

            int numberOfOwners = 0;
            double stateOfUse = 0, markup = 0;

            String description = null, brand = null;
            double basePrice = 0;

            int x = 0, y = 0, z = 0, collectionYear = 0;
            String material = null;
            double discount = 0;

            TShirt.Size tshirtSize = null;
            TShirt.Pattern tshirtPattern = null;

            boolean hasLaces = false;
            int shoesSize = 0;
            String color = null;

            for (int j = i+2; j < line.length; j++) {
                switch (line[j]) {
                    case "USED" -> {
                        used = true;
                        numberOfOwners = Integer.parseInt(line[++j]);
                        stateOfUse = Double.parseDouble(line[++j]);
                    }
                    case "PREMIUM" -> {
                        premium = true;
                        markup = Double.parseDouble(line[++j]);
                    }
                    case "BAG" -> {
                        type = Type.bag;
                        description = line[++j];
                        brand = line[++j];
                        basePrice = Double.parseDouble(line[++j]);
                        x = Integer.parseInt(line[++j]);
                        y = Integer.parseInt(line[++j]);
                        z = Integer.parseInt(line[++j]);
                        material = line[++j];
                        collectionYear = Integer.parseInt(line[++j]);
                        discount = Double.parseDouble(line[++j]);
                    }
                    case "T-SHIRT" -> {
                        type = Type.tShirt;
                        description = line[++j];
                        brand = line[++j];
                        basePrice = Double.parseDouble(line[++j]);
                        switch (line[++j]) {
                            case "S" -> tshirtSize = TShirt.Size.S;
                            case "M" -> tshirtSize = TShirt.Size.M;
                            case "L" -> tshirtSize = TShirt.Size.L;
                            case "XL" -> tshirtSize = TShirt.Size.XL;
                        }
                        switch (line[++j]) {
                            case "SOLID" -> tshirtPattern = TShirt.Pattern.SOLID;
                            case "STRIPES" -> tshirtPattern = TShirt.Pattern.STRIPES;
                            case "PALM TREES" -> tshirtPattern = TShirt.Pattern.PALM_TREES;
                        }
                    }
                    case "SHOES" -> {
                        type = Type.shoes;
                        description = line[++j];
                        brand = line[++j];
                        basePrice = Double.parseDouble(line[++j]);
                        shoesSize = Integer.parseInt(line[++j]);
                        hasLaces = line[++j].equals("with laces");
                        color = line[++j];
                        collectionYear = Integer.parseInt(line[++j]);
                        discount = Double.parseDouble(line[++j]);
                    }
                }
            }

            Article article = null;
            if (type.equals(Type.bag)) {
                if (used && premium) {
                    article = this.articleFactory.createBag(description, brand, basePrice, numberOfOwners, stateOfUse, markup, carrier, sellerID, new int[]{x, y, z}, material, collectionYear, discount);
                } else if (used) {
                    article = this.articleFactory.createBag(description, brand, basePrice, numberOfOwners, stateOfUse, carrier, sellerID, new int[]{x, y, z}, material, collectionYear, discount);
                } else if (premium) {
                    article = this.articleFactory.createBag(description, brand, basePrice, markup, carrier, sellerID, new int[]{x, y, z}, material, collectionYear, discount);
                } else {
                    article = this.articleFactory.createBag(description, brand, discount, carrier, sellerID, new int[]{x, y, z}, material, collectionYear, discount);
                }
            } else if (type.equals(Type.tShirt)) {
                if (used) {
                    article = this.articleFactory.createTShirt(description, brand, basePrice, numberOfOwners, stateOfUse, carrier, sellerID, tshirtSize, tshirtPattern);
                } else {
                    article = this.articleFactory.createTShirt(description, brand, basePrice, carrier, sellerID, tshirtSize, tshirtPattern);
                }
            } else if (type.equals(Type.shoes)) {
                if (used && premium) {
                    article = this.articleFactory.createShoes(description, brand, basePrice, numberOfOwners, stateOfUse, markup, carrier, sellerID, shoesSize, hasLaces, color, collectionYear, discount);
                } else if (used) {
                    article = this.articleFactory.createShoes(description, brand, basePrice, numberOfOwners, stateOfUse, carrier, sellerID, shoesSize, hasLaces, color, collectionYear, discount);
                } else if (premium) {
                    article = this.articleFactory.createShoes(description, brand, basePrice, markup, carrier, sellerID, shoesSize, hasLaces, color, collectionYear, discount);
                } else {
                    article = this.articleFactory.createShoes(description, brand, basePrice, carrier, sellerID, shoesSize, hasLaces, color, collectionYear, discount);
                }
            }

            if (!type.equals(Type.other)) this.appModel.sellArticle(sellerID, article);
        }
    }

    public void parseSimulation (String fileName) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("src/Resources/CSV Files/" + fileName), StandardCharsets.UTF_8);

            String[] firstLine = lines.get(0).split(","); // the csv file starts with the initial date
            String dateStr = firstLine[0];
            String timeStr = firstLine[1];

            DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = formatterDate.parse(dateStr + " " + timeStr);

            this.appModel.setDate(date);

            for (int i = 1; i < lines.size(); i++) {

                try {
                    String[] line = lines.get(i).split(","); // split the line into fields
                    String dateStrField = line[0];
                    String timeStrFiled = line[1];

                    Date dateAndTime = formatterDate.parse(dateStrField + " " + timeStrFiled);
                    LocalDate justDate = LocalDate.parse(dateStrField, formatterLocalDate);

                    this.appModel.timeMachine(justDate); // iter to the day
                    this.appModel.setDate(dateAndTime); // set the correct time

                    switch (line[2]) {
                        case "CreateUser" -> {
                            if (line.length == 9)
                                this.appModel.addUserToCatalog(line[4], line[3], line[5], line[6], line[7], line[8]);
                        }
                        case "CreateCarrier" -> {
                            if (line.length == 9 || line.length == 10) {
                                String[] carrierFields = Arrays.copyOfRange(line, 3, line.length);
                                this.createCarrier(carrierFields);
                            }
                        }
                        case "SellArticle" -> this.sellArticle(line, 3);
                        case "AddToCart" -> {
                            if (line.length == 5) {
                                UUID buyerID = this.appModel.getUserID(line[3]);
                                try {
                                    UUID articleID = UUID.fromString(line[4]);
                                    this.appModel.addArticleToCart(buyerID, articleID);

                                }
                                catch (IllegalArgumentException e) {
                                    AppLogger.logError(e);
                                    UUID articleID = this.appModel.getArticle(line[4]);
                                    this.appModel.addArticleToCart(buyerID, articleID);
                                }
                            }
                        }
                        case "BuyOrder" -> {
                            if (line.length == 5) {
                                UUID buyerID = this.appModel.getUserID(line[3]);
                                double userMoney = Double.parseDouble(line[4]);

                                if (this.appModel.getCartPrice(buyerID) <= userMoney)
                                    this.appModel.buyOrder(buyerID);
                            }
                        }
                        case "CancelOrder" -> {
                            if (line.length == 5) {
                                UUID buyerID = this.appModel.getUserID(line[3]);
                                int orderNumber = Integer.parseInt(line[4]);
                                UUID orderID = this.appModel.getOrderID(buyerID, orderNumber);
                                this.appModel.cancelOrder(buyerID, orderID);
                            }
                        }
                        case "ReturnOrder" -> {
                            if (line.length == 5) {
                                UUID buyerID = this.appModel.getUserID(line[3]);
                                int orderNumber = Integer.parseInt(line[4]);
                                UUID orderID = this.appModel.getOrderID(buyerID, orderNumber);
                                this.appModel.returnOrder(buyerID, orderID);
                            }
                        }
                        case "ChangeUserValue" -> {
                            if (line.length == 6) {
                                UUID user = this.appModel.getUserID(line[3]);
                                switch (line[4]) {
                                    case "NAME" -> this.appModel.changeNameOfUser(user, line[5]);
                                    case "COUNTRY" -> this.appModel.changeCountryOfUser(user, line[5]);
                                    case "CITY" -> this.appModel.changeCityOfUser(user, line[5]);
                                    case "STREET" -> this.appModel.changeStreetOfUser(user, line[5]);
                                    case "POSTAL-CODE" -> this.appModel.changePostalCodeOfUser(user, line[5]);
                                }
                            }
                        }
                        case "ChangeCarrierValue" -> {
                            if (line.length == 6) {
                                Carrier carrier = this.appModel.getCarrier(line[3]);
                                switch (line[4]) {
                                    case "BASE-PRICE-SMALL" -> carrier.setBasePriceSmall(Double.parseDouble(line[5]));
                                    case "BASE-PRICE-MEDIUM" -> carrier.setBasePriceMedium(Double.parseDouble(line[5]));
                                    case "BASE-PRICE-BIG" -> carrier.setBasePriceBig(Double.parseDouble(line[5]));
                                    case "TAX" -> carrier.setTax(Double.parseDouble(line[5]));
                                    case "PROFIT-RATE" -> carrier.setProfitRate(Double.parseDouble(line[5]));
                                }
                            }
                        }
                        case "UpdateTime" -> {
                            // the for loop already does it
                        }
                        case "SaveSate" -> {
                            this.state.saveData(this.appModel);
                        }
                    }
                }
                catch (ParseException | AlreadyExistsInCatalogException | UserNotFoundException | NumberFormatException | OrderNotFoundException
                       | NotFoundInCatalogException | ArrayIndexOutOfBoundsException | ArticleNegativeNumberException | ReturnOrderException e) {
                    AppLogger.logError(e);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException | IOException | ParseException e) {
            AppLogger.logError(e);
        }
    }
}