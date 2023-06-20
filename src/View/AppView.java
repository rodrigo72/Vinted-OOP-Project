package View;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class AppView implements View {
    private ResourceBundle bundle;

    public AppView () {
        this.bundle = ResourceBundle.getBundle("output", Locale.getDefault());
    }

    public ResourceBundle getBundle() {
        return this.bundle;
    }

    public void updateLanguage() {
        this.bundle = ResourceBundle.getBundle("output");
    }

    public void enterFileName() { printBundleString("enter_file_name"); }

    public void errorFileNotFound() { printBundleString("error_file_not_found"); }
    public void errorLoading() { printBundleString("error_loading_file"); }
    public void errorClassNotFound() { printBundleString("error_class_not_found"); }
    public void errorSavingObject() { printBundleString("error_saving_object"); }
    public void errorInput() { printBundleString("error_input");}
    public void errorNoUsersFound() { printBundleString("error_no_users_found"); }
    public void errorNoCarriersFound() { printBundleString("error_no_carriers_found"); }

    public void userRegistrationError() { printBundleString("user_registration_error"); }
    public void userLoginHeader() { printBundleString("user_login_header"); }
    public void userLoginEmail() { printBundleString("user_login_email_prompt"); }
    public void userInvalidLogin() { printBundleString("user_invalid_login"); }

    public void userRegistrationHeader() { printBundleString("user_registration_header"); }
    public void userRegistrationName() { printBundleString("user_registration_name_prompt"); }
    public void userRegistrationEmail() { printBundleString("user_registration_email_prompt"); }
    public void userRegistrationCountry() { printBundleString("user_registration_country_prompt"); }
    public void userRegistrationCity() { printBundleString("user_registration_city_prompt"); }
    public void userRegistrationStreet() { printBundleString("user_registration_street_prompt"); }
    public void userRegistrationPostalCode() { printBundleString("user_registration_postal_code_prompt"); }

    public void carrierRegistrationHeader() { printBundleString("carrier_registration_header"); }
    public void carrierRegistrationName() { printBundleString("carrier_registration_name_prompt"); }
    public void carrierRegistrationSmallBasePrice() { printBundleString("carrier_registration_small_base_price_prompt"); }
    public void carrierRegistrationMediumBasePrice() { printBundleString("carrier_registration_medium_base_price_prompt"); }
    public void carrierRegistrationBigBasePrice() { printBundleString("carrier_registration_big_base_price_prompt"); }
    public void carrierRegistrationTax() { printBundleString("carrier_registration_tax_prompt"); }
    public void carrierRegistrationProfit() { printBundleString("carrier_registration_profit_prompt"); }
    public void carrierRegistrationIsPremium() { printBundleString("carrier_registration_is_premium"); }
    public void carrierRegistrationPremiumPrice() { printBundleString("carrier_registration_premium_price"); }
    public void carrierRegistrationError() { printBundleString("carrier_registration_error"); }

    public void statisticsStartDate() { printBundleString("statistics_inf_date"); }
    public void statisticsEndDate() { printBundleString("statistics_sup_date"); }


    public void startingMenu() {
        String sb = "\n" +
                this.bundle.getString("starting_menu_header") + "\n" +
                "1 - " + this.bundle.getString("starting_menu_read") + "\n" +
                "2 - " + this.bundle.getString("starting_menu_load") + "\n" +
                "0 - " + this.bundle.getString("starting_menu_continue") + "\n";
        printString(sb);
    }

    public void parseMenu(){
        String sb = "\n" +
                this.bundle.getString("parse_menu_header") + "\n" +
                "1 - " + this.bundle.getString("parse_menu_users") + "\n" +
                "2 - " + this.bundle.getString("parse_menu_carriers") + "\n" +
                "3 - " + this.bundle.getString("parse_menu_articles") + "\n" +
                "4 - " + this.bundle.getString("parse_menu_simulation") + "\n" +
                "0 - " + this.bundle.getString("parse_menu_exit") + "\n";
        printString(sb);
    }

    public void appMenu() {
        String sb = "\n" +
                this.bundle.getString("app_menu_header") + "\n" +
                "1 - " + this.bundle.getString("app_menu_register") + "\n" +
                "2 - " + this.bundle.getString("app_menu_login") + "\n" +
                "3 - " + this.bundle.getString("app_menu_statistics") + "\n" +
                "4 - " + this.bundle.getString("app_menu_save") + "\n" +
                "5 - " + this.bundle.getString("app_menu_view_data") + "\n" +
                "6 - " + this.bundle.getString("app_menu_change_language") + "\n" +
                "0 - " + this.bundle.getString("app_menu_exit") + "\n";
        printString(sb);
    }

    public void viewData() {
        String sb = "\n" +
                this.bundle.getString("view_data_header") + "\n" +
                "1 - " + this.bundle.getString("view_data_users") + "\n" +
                "2 - " + this.bundle.getString("view_data_carriers") + "\n" +
                "3 - " + this.bundle.getString("view_data_articles") + "\n" +
                "4 - " + this.bundle.getString("view_data_orders") + "\n" +
                "0 - " + this.bundle.getString("view_data_exit") + "\n";
        printString(sb);
    }

    public void statistics() {
        String sb = "\n" +
                this.bundle.getString("statistics_header") + "\n" +
                "1 - " + this.bundle.getString("statistics_user_highest_earner") + "\n" +
                "2 - " + this.bundle.getString("statistics_user_highest_spender") + "\n" +
                "3 - " + this.bundle.getString("statistics_carrier_highest_earner") + "\n" +
                "4 - " + this.bundle.getString("statistics_user_order_earnings") + "\n" +
                "5 - " + this.bundle.getString("statistics_user_order_spending") + "\n" +
                "6 - " + this.bundle.getString("statistics_vintage_total_earned") + "\n" +
                "0 - " + this.bundle.getString("statistics_exit") + "\n";
        printString(sb);
    }

    public void statisticsChoseTime() {
        String sb = "\n" +
                "1 - " + this.bundle.getString("statistics_user_all_time") + "\n" +
                "2 - " + this.bundle.getString("statistics_user_time_period") + "\n" +
                "0 - " + this.bundle.getString("statistics_exit") + "\n";
        printString(sb);
    }

    public void languages() {
        String sb = "\n" +
                this.bundle.getString("language_select") + "\n" +
                "1 - " + this.bundle.getString("language_english") + "\n" +
                "2 - " + this.bundle.getString("language_portuguese") + "\n" +
                "3 - " + this.bundle.getString("language_spanish") + "\n" +
                "0 - " + this.bundle.getString("language_exit") + "\n";
        printString(sb);
    }

    public void register() {
        String sb = "\n" +
                this.bundle.getString("register_header") + "\n" +
                "1 - " + this.bundle.getString("register_user") + "\n" +
                "2 - " + this.bundle.getString("register_carrier") + "\n";
        printString(sb);
    }

    public void displayCatalog (List<String> names) {
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append("\n");
            sb.append(name);
        }
        printString(sb.toString());
    }

    public void displayNumberedList (List<String> elements) {
        int iteration = 1;
        StringBuilder sb = new StringBuilder();
        for (String name : elements) {
            sb.append("\n");
            sb.append(iteration).append(" - ").append(name);
            iteration++;
        }
        printString(sb.toString());
    }

    public void printBundleString (String str) {
        printString(this.bundle.getString(str));
    }
}
