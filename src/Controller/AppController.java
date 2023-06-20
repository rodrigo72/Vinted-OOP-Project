package Controller;

import Controller.Parser;
import Logs.AppLogger;
import Model.AppModel;
import Model.Data.Exceptions.AlreadyExistsInCatalogException;
import Model.State;
import Model.Types.Exceptions.ArticleNegativeNumberException;
import Model.Types.Exceptions.CarrierNotFoundException;
import Model.Types.Exceptions.UserNotFoundException;
import View.AppView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

public class AppController {
    private final State state;
    private UserController userController;
    private Parser parser;
    private AppView view;
    private AppModel appModel;
    private int option;
    private Scanner scanner;

    public AppController (Scanner scanner) {
        Locale.setDefault(new Locale.Builder().setLanguage("en").build());
        this.state = new State();
        this.scanner = scanner;
        this.view = new AppView();
        this.appModel = null;
        this.userController = null;
        this.option = -1;
    }

    public AppController (AppView appView, AppModel appModel, Scanner scanner) {
        this.state = new State();
        this.scanner = scanner;
        this.appModel = appModel;
        this.view = appView;
        this.userController = new UserController(scanner, appModel, appView);
    }

    public void setModel (AppModel appModel) {
        this.appModel = appModel;
    }
    public void setAppView (AppView view) {
        this.view = view;
    }
    public void setScanner (Scanner scanner) {
        this.scanner = scanner;
    }

    // first menu (appears once)
    public void runController() {

        do {
            try {
                this.view.startingMenu();
                this.option = this.scanner.nextInt();

                switch (this.option) {
                    case 1 -> { // read files
                        this.appModel = new AppModel();
                        do {
                            this.view.parseMenu();
                            this.option = this.scanner.nextInt();
                            switch (this.option) {
                                case 1 -> {
                                    this.view.enterFileName();
                                    this.scanner.nextLine();
                                    String fileName = this.scanner.nextLine();
                                    this.parser = new Parser(this.appModel, this.state, this.view);
                                    this.parser.parseUserFile(fileName);
                                }
                                case 2 -> {
                                    this.view.enterFileName();
                                    this.scanner.nextLine();
                                    String fileName = this.scanner.nextLine();
                                    this.parser = new Parser(this.appModel, this.state, this.view);
                                    this.parser.parseCarrierFile(fileName);
                                }
                                case 3 -> {
                                    this.view.enterFileName();
                                    this.scanner.nextLine();
                                    String fileName = this.scanner.nextLine();
                                    this.parser = new Parser(this.appModel, this.state, this.view);
                                    this.parser.parseArticleFile(fileName);
                                }
                                case 4 -> {
                                    this.view.enterFileName();
                                    this.scanner.nextLine();
                                    String fileName = this.scanner.nextLine();
                                    this.parser = new Parser(this.appModel, this.state, this.view);
                                    this.parser.parseSimulation(fileName);
                                }
                            }
                        } while (this.option != 0);
                    }
                    case 2 -> { // load state
                        try {
                            this.appModel = state.loadData();
                        } catch (FileNotFoundException e) {
                            AppLogger.logError(e);
                            this.view.errorFileNotFound();
                            this.appModel = null;
                        } catch (IOException e) {
                            AppLogger.logError(e);
                            this.view.errorLoading();
                        } catch (ClassNotFoundException e) {
                            AppLogger.logError(e);
                            this.view.errorClassNotFound();
                        }
                        if (this.appModel == null) {
                            this.appModel = new AppModel();
                        }
                        this.option = 0;
                    }
                    case 0 -> {
                        if (this.appModel == null) {
                            this.appModel = new AppModel();
                        }
                    }
                }
            } catch (InputMismatchException e) {
                AppLogger.logError(e);
                this.view.errorInput();
                this.scanner.nextLine();
            }
        } while (this.option != 0);

        this.userController = new UserController(this.scanner, this.appModel, this.view);

        try {
            this.appModel.updateArticleCatalog();
        }
        catch (ArticleNegativeNumberException e) {
            AppLogger.logError(e);
        }
    }

    // main menu
    public void menu() {
        this.option = -1;
        do {
            try {
                this.view.appMenu();
                this.option = this.scanner.nextInt();

                switch (this.option) {
                    case 1 -> { // register users and carriers
                        this.view.register();
                        this.scanner.nextLine();
                        String type = this.scanner.nextLine();

                        if (type.equals("1")) {
                            this.register(1);
                        } else if (type.equals("2")) {
                            this.register(2);
                        }
                    }
                    case 2 -> { // user login
                        this.scanner.nextLine();
                        this.login();
                    }
                    case 3 -> { // statistics
                        this.scanner.nextLine();
                        try {
                            this.statistics();
                        } catch (UserNotFoundException e) {
                            AppLogger.logError(e);
                            this.view.errorNoUsersFound();
                        } catch (CarrierNotFoundException e) {
                            AppLogger.logError(e);
                            this.view.errorNoCarriersFound();
                        }
                    }
                    case 4 -> { // save state
                        try {
                            state.saveData(appModel);
                        } catch (FileNotFoundException e) {
                            AppLogger.logError(e);
                            this.view.errorFileNotFound();
                        } catch (IOException e) {
                            AppLogger.logError(e);
                            this.view.errorSavingObject();
                        }
                    }
                    case 5 -> { // view data
                        int choice;
                        this.scanner.nextLine();
                        do {
                            this.view.viewData();
                            choice = this.scanner.nextInt();
                            if (choice == 1) { // users
                                this.viewUsers();
                            } else if (choice == 2) { // carriers
                                this.viewCarriers();
                            } else if (choice == 3) { // articles
                                this.viewArticles();
                            } else if (choice == 4) { // orders
                                this.viewOrders();
                            }
                        } while (choice != 0);
                    }
                    case 6 -> { // language
                        this.scanner.nextLine();
                        int choice;
                        boolean valid = false;
                        do {
                            this.view.languages();
                            choice = this.scanner.nextInt();
                            if (choice == 1) { // english
                                this.updateLanguage("en");
                                valid = true;
                            } else if (choice == 2) { // portuguese
                                this.updateLanguage("pt");
                                valid = true;
                            } else if (choice == 3) { // spanish
                                this.updateLanguage("es");
                                valid = true;
                            }
                        } while (choice != 0 && !valid);

                    }
                }
            } catch (InputMismatchException e) {
                AppLogger.logError(e);
                this.view.errorInput();
                this.scanner.nextLine();
            }
        } while (this.option != 0);
    }

    // auxiliary method for the main menu
    private void updateLanguage (String str) {
        if (!Locale.getDefault().getCountry().equals(str)) {
            Locale.setDefault(new Locale.Builder().setLanguage(str).build());
            this.view.updateLanguage();
            this.userController.updateLanguage();
        }
    }

    // auxiliary method for the main menu
    private void login() {
        boolean exit = false, logged = false;
        this.view.userLoginHeader();
        String email;
        do {
            this.view.userLoginEmail();
            email = this.scanner.nextLine();
            if (email.equals("0")) {
                exit = true;
            } else if (!this.appModel.login(email)) {
                this.view.userInvalidLogin();
            } else {
                logged = true;
            }
        } while (!exit && !logged);

        if (logged) {
            this.userController.setUser(this.appModel.getUser(email));
            this.userController.run();
        }
    }

    // auxiliary method for the main menu
    private void register (int type) {
        if (type == 1) {
            this.view.userRegistrationHeader();
            this.view.userRegistrationName();
            String name = this.scanner.nextLine();
            this.view.userRegistrationEmail();
            String email = this.scanner.nextLine();
            this.view.userRegistrationCountry();
            String country = this.scanner.nextLine();
            this.view.userRegistrationCity();
            String city = this.scanner.nextLine();
            this.view.userRegistrationStreet();
            String street = this.scanner.nextLine();
            this.view.userRegistrationPostalCode();
            String postalCode = this.scanner.nextLine();

            try {
                this.appModel.addUserToCatalog(name, email, country, city, street, postalCode);
            }
            catch (AlreadyExistsInCatalogException e) {
                AppLogger.logError(e);
                this.view.userRegistrationError();
            }
        }
        else if (type == 2) {
            this.view.carrierRegistrationHeader();
            this.view.carrierRegistrationName();
            String name = this.scanner.nextLine();
            this.view.carrierRegistrationSmallBasePrice();
            double smallBasePrice = this.scanner.nextDouble();
            this.view.carrierRegistrationMediumBasePrice();
            double mediumBasePrice = this.scanner.nextDouble();
            this.view.carrierRegistrationBigBasePrice();
            double bigBasePrice = this.scanner.nextDouble();
            this.view.carrierRegistrationTax();
            double tax = this.scanner.nextDouble();
            this.view.carrierRegistrationProfit();
            double profit = this.scanner.nextDouble();
            this.view.carrierRegistrationIsPremium();
            boolean isPremiumB = false;
            boolean valid = false;
            do {
                this.scanner.nextLine();
                String isPremium = this.scanner.nextLine();
                if (isPremium.equals("y") || isPremium.equals("Y") || isPremium.equals("1")) {
                    isPremiumB = true;
                    valid = true;
                } else if (isPremium.equals("n") || isPremium.equals("N") || isPremium.equals("2")) {
                    valid = true;
                }
            } while (!valid);

            if (isPremiumB) {
                this.view.carrierRegistrationPremiumPrice();
                double additionalPrice = this.scanner.nextDouble();
                try { this.appModel.addPremiumCarrierToCatalog(name, smallBasePrice, mediumBasePrice, bigBasePrice, tax, profit, additionalPrice);}
                catch (AlreadyExistsInCatalogException e) {
                    AppLogger.logError(e);
                    this.view.carrierRegistrationError();
                }
            } else {
                try { this.appModel.addCarrierToCatalog(name, smallBasePrice, mediumBasePrice, bigBasePrice, tax, profit);}
                catch (AlreadyExistsInCatalogException e) {
                    AppLogger.logError(e);
                    this.view.carrierRegistrationError();
                }
            }
        }
    }

    // used for displaying statistics (enables code reuse by using Function and Supplier)
    private void highestUser(Supplier<String> supplier, Function<LocalDate[], String> function) {
        int choice;
        do {
            this.view.statisticsChoseTime();
            choice = this.scanner.nextInt();
            if (choice == 1) {
                this.view.printString(supplier.get());
            } else if (choice == 2) {
                this.scanner.nextLine();
                this.view.statisticsStartDate();
                String startDate = this.scanner.nextLine();
                this.view.statisticsEndDate();
                String endDate = this.scanner.nextLine();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate infDate, supDate;
                try {
                    infDate = LocalDate.parse(startDate, formatter);
                    supDate = LocalDate.parse(endDate, formatter);
                    LocalDate[] dates = new LocalDate[] {infDate, supDate};

                    this.view.printString(function.apply(dates));

                } catch (DateTimeParseException e) {
                    this.view.errorInput();
                }
            }
        } while (choice != 0);
    }

    private void sortedUsers (Function<LocalDate[], ArrayList<String>> function) {
        this.view.statisticsStartDate();
        String startDate = this.scanner.nextLine();
        this.view.statisticsEndDate();
        String endDate = this.scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate infDate, supDate;
        try {
            infDate = LocalDate.parse(startDate, formatter);
            supDate = LocalDate.parse(endDate, formatter);
            LocalDate[] dates = new LocalDate[] {infDate, supDate};

            this.view.displayNumberedList(function.apply(dates));
        } catch (DateTimeParseException e) {
            this.view.errorInput();
        }
    }
    private void statistics() throws UserNotFoundException, CarrierNotFoundException {
        int choice;
        do {
            this.view.statistics();
            choice = this.scanner.nextInt();

            if (choice == 1) { // highest earner
                this.scanner.nextLine();
                this.highestUser(() -> {
                            try { return this.appModel.getUserWhoEarnedTheMost();}
                            catch (UserNotFoundException e) { AppLogger.logError(e); }
                            return null;
                        }
                        , (LocalDate[] dates) -> {
                            try { return this.appModel.getUserWhoEarnedTheMostInPeriodOfTime(dates[0], dates[1]); }
                            catch (UserNotFoundException e) { AppLogger.logError(e); }
                            return null;
                        });

            } else if (choice == 2) { // highest spender
                this.scanner.nextLine();
                this.highestUser(() -> {
                            try { return this.appModel.getUserWhoSpentTheMost(); }
                            catch (UserNotFoundException e) { AppLogger.logError(e); }
                            return null;
                        }
                        , (LocalDate[] dates) -> {
                            try { return this.appModel.getUserWhoSpentTheMostInPeriodOfTime(dates[0], dates[1]); }
                            catch (UserNotFoundException e) { AppLogger.logError(e); }
                            return null;
                        });
            } else if (choice == 3) { // richest carrier
                this.view.printString(this.appModel.getCarrierThatEarnedTheMost());
            } else if (choice == 4) { // sorted users by earnings
                this.scanner.nextLine();
                this.sortedUsers((LocalDate[] dates) -> this.appModel.sortedHighestEarningInPeriodOfTimeAsStrings(dates[0], dates[1]));

            } else if (choice == 5) { // sorted users by spending
                this.scanner.nextLine();
                this.sortedUsers((LocalDate[] dates) -> this.appModel.sortedHighestSpendingInPeriodOfTimeAsStrings(dates[0], dates[1]));
            } else if (choice == 6) { // vintage total earned
                this.view.printString(Double.toString(this.appModel.getVintageMoneyEarned()));
            }

        } while (choice != 0);
    }

    // View data methods
    private void viewUsers() {
        ArrayList<String> articlesAsStrings = this.appModel.getAllUsersAsString();
        this.view.displayCatalog(articlesAsStrings);

        this.scanner.nextLine();
    }

    private void viewArticles() {
        ArrayList<String> articlesAsStrings = this.appModel.getAllArticlesAsString();
        this.view.displayCatalog(articlesAsStrings);

        this.scanner.nextLine();
    }

    private void viewCarriers() {
        ArrayList<String> articlesAsStrings = this.appModel.getAllCarriersAsString();
        this.view.displayCatalog(articlesAsStrings);

        this.scanner.nextLine();
    }

    private void viewOrders() {
        ArrayList<String> articlesAsStrings = this.appModel.getAllOrdersAsString();
        this.view.displayCatalog(articlesAsStrings);

        this.scanner.nextLine();
    }

}