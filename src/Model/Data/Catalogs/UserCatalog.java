package Model.Data.Catalogs;

import Model.Data.Exceptions.AlreadyExistsInCatalogException;
import Model.Types.Comparators.UserMoneySpentInPeriodOfTimeComparator;
import Model.Types.Residence;
import Model.Types.User;
import Model.Types.Comparators.UserMoneyEarnedInPeriodOfTimeComparator;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserCatalog extends GenericCatalog<User> {
    private Map<String, User> usedEmails;
    public UserCatalog() {
        super();
        this.usedEmails = new HashMap<>();
    }
    public UserCatalog (Map<UUID, User> users) {
        super(users);
        this.usedEmails = new HashMap<>();
    }
    public UserCatalog (UserCatalog userCatalog) {
        this.catalog = userCatalog.getCatalog();
        this.usedEmails = userCatalog.getUsedEmails();
    }

    public Map<String, User> getUsedEmails() {
        return usedEmails;
    }
    public void setUsedEmails(Map<String, User> usedEmails) {
        this.usedEmails = usedEmails;
    }

    @Override
    public String toString() {
        return "UserCatalog {" +
                " users = " + this.catalog +
                " }";
    }

    @Override
    public void add (User user) {
        if (!this.usedEmails.containsKey(user.getEmail())) {
            super.add(user);
            this.usedEmails.put(user.getEmail(), user);
        }
    }

    public void add (String name, String email, String country, String city, String street, String postalCode) throws AlreadyExistsInCatalogException {
        if (!this.exists(email)) {
            Residence residence = new Residence(country, city, street, postalCode);
            User user = new User(name, email, residence);
            super.add(user);
            this.usedEmails.put(email, user);
        } else {
            throw new AlreadyExistsInCatalogException();
        }
    }

    public User getHighestEarnerAllTime() {
        return getHighestAllTime(User::getMoneyEarned);
    }

    public User getHighestSpenderAllTime() {
        return getHighestAllTime(User::getMoneySpent);
    }

    public User getHighestAllTime(Function<User, Double> moneyFunction) {
        return getUser(moneyFunction);
    }

    private User getUser(Function<User, Double> moneyFunction) {
        double highest = -1;
        User chosen = null;
        for (User user : this.catalog.values()) {
            double userMoney = moneyFunction.apply(user);
            if (userMoney > highest) {
                highest = userMoney;
                chosen = user;
            }
        }
        return chosen;
    }

    public User getHighestEarnerPeriodOfTime (LocalDate infDate, LocalDate supDate) {
        return this.getHighestInPeriodOfTime(user -> user.getMoneyEarnedInPeriodOfTime(infDate, supDate));
    }

    public User getHighestSpenderPeriodOfTime (LocalDate infDate, LocalDate supDate) {
        return this.getHighestInPeriodOfTime(user -> user.getMoneySpentInPeriodOfTime(infDate, supDate));
    }

    public User getHighestInPeriodOfTime(Function<User, Double> moneyFunction) {
        return getUser(moneyFunction);
    }

    public ArrayList<User> sortedHighestEarningInPeriodOfTime (LocalDate infDate, LocalDate supDate) {
        ArrayList<User> users = new ArrayList<>(this.catalog.values());
        UserMoneyEarnedInPeriodOfTimeComparator comparator = new UserMoneyEarnedInPeriodOfTimeComparator(infDate, supDate);
        users.sort(comparator);
        return users;
    }

    public ArrayList<String> sortedHighestEarningInPeriodOfTimeAsStrings (LocalDate infDate, LocalDate supDate) {
        return this.sortedHighestEarningInPeriodOfTime(infDate, supDate)
                .stream()
                .map(User::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<User> sortedHighestSpendingInPeriodOfTime (LocalDate infDate, LocalDate supDate) {
        ArrayList<User> users = new ArrayList<>(this.catalog.values());
        UserMoneySpentInPeriodOfTimeComparator comparator = new UserMoneySpentInPeriodOfTimeComparator(infDate, supDate);
        users.sort(comparator);
        return users;
    }

    public ArrayList<String> sortedHighestSpendingInPeriodOfTimeAsStrings (LocalDate infDate, LocalDate supDate) {
        return this.sortedHighestSpendingInPeriodOfTime(infDate, supDate)
                .stream()
                .map(User::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean exists (String email) {
        return this.usedEmails.containsKey(email);
    }
    public User get (String email) {
        return this.usedEmails.get(email);
    }
}
