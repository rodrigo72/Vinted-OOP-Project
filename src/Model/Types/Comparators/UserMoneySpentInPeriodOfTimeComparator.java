package Model.Types.Comparators;

import Model.Types.User;

import java.time.LocalDate;

public class UserMoneySpentInPeriodOfTimeComparator extends UserMoneyInPeriodOfTimeComparator{
    public UserMoneySpentInPeriodOfTimeComparator (LocalDate infDate, LocalDate supDate) {
        super(infDate, supDate);
    }
    @Override
    public int compare(User u1, User u2) {
        double moneySpent1 = u1.getMoneySpentInPeriodOfTime(infDate, supDate);
        double moneySpent2 = u2.getMoneySpentInPeriodOfTime(infDate, supDate);
        return Double.compare(moneySpent2, moneySpent1);
    }
}
