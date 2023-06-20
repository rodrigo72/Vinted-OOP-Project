package Model.Types.Comparators;

import Model.Types.User;

import java.time.LocalDate;
import java.util.Comparator;

public class UserMoneyEarnedInPeriodOfTimeComparator extends UserMoneyInPeriodOfTimeComparator {

    public UserMoneyEarnedInPeriodOfTimeComparator (LocalDate infDate, LocalDate supDate) {
        super(infDate, supDate);
    }
    @Override
    public int compare(User u1, User u2) {
        double moneyEarned1 = u1.getMoneyEarnedInPeriodOfTime(infDate, supDate);
        double moneyEarned2 = u2.getMoneyEarnedInPeriodOfTime(infDate, supDate);
        return Double.compare(moneyEarned2, moneyEarned1);
    }
}
