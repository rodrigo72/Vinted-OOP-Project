package Model.Types.Comparators;

import Model.Types.User;

import java.time.LocalDate;
import java.util.Comparator;

public abstract class UserMoneyInPeriodOfTimeComparator implements Comparator<User> {
    protected LocalDate infDate;
    protected LocalDate supDate;

    protected UserMoneyInPeriodOfTimeComparator (LocalDate infDate, LocalDate supDate) {
        this.infDate = infDate;
        this.supDate = supDate;
    }

}
