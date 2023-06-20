package View;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class UserView implements View {

    private ResourceBundle bundle;

    public UserView () {
        this.bundle = ResourceBundle.getBundle("output", Locale.getDefault());
    }

    public UserView (AppView appView) {
        this.bundle = appView.getBundle();
    }

    public void updateLanguage() {
        this.bundle = ResourceBundle.getBundle("output");
    }

    public void articleNumberOfOwners() { printBundleString("article_number_of_owners_prompt"); }
    public void articleStateOfUse() { printBundleString("article_state_of_use_prompt"); }
    public void articleMarkup() { printBundleString("article_markup"); }

    public void articleDescription() { printBundleString("article_description_prompt"); }
    public void articleBrand() { printBundleString("article_brand_prompt"); }
    public void articleBasePrice() { printBundleString("article_base_price_prompt"); }

    public void articleShirtSize() { printBundleString("article_shirt_size_prompt"); }
    public void articleShirtPatter() { printBundleString("article_shirt_pattern_prompt"); }
    public void articleBagHeight() { printBundleString("article_bag_height_prompt"); }
    public void articleBagWidth() { printBundleString("article_bag_width_prompt"); }
    public void articleBagLength() { printBundleString("article_bag_length_prompt"); }
    public void articleBagMaterial() { printBundleString("article_bag_material_prompt"); }
    public void articleBagCollectionYear() { printBundleString("article_bag_collection_year_prompt"); }
    public void articleBagDiscount() { printBundleString("article_bag_discount_prompt"); }
    public void articleShoesSize() { printBundleString("article_shoes_size_prompt"); }
    public void articleShoesHasLaces() { printBundleString("article_shoes_has_laces_prompt"); }
    public void articleShoesColor() { printBundleString("article_shoes_color_prompt"); }
    public void articleShoesCollectionYear() { printBundleString("article_shoes_collection_year_prompt"); }
    public void articleShoesDiscount() { printBundleString("article_shoes_discount_prompt"); }

    public void errorCarrier() { printBundleString("error_carrier"); }
    public void errorArticle() { printBundleString("error_article"); }
    public void errorInput() { printBundleString("error_input"); }
    public void errorUserNotFount() { printBundleString("error_user_not_found"); }

    public void error() { printBundleString("error"); }

    public void cartAdd() { printBundleStringWithExit("cart_add");}
    public void cartView() { printBundleStringWithExit("cart_view");}
    public void cartNumberToRemove() { printBundleStringWithExit("cart_number_to_remove");}
    public void cartRemovedArticle() { printBundleString("cart_removed_article");}
    public void forSaleArticlesView() { printBundleStringWithExit("for_sale_articles_view");}
    public void forSaleArticlesRemove() { printBundleStringWithExit("for_sale_articles_remove");}
    public void forSaleArticlesAdded() { printBundleString("for_sale_articles_added");}
    public void forSaleArticlesRemoved() { printBundleString("for_sale_articles_removed");}
    public void soldArticlesView() { printBundleStringWithExit("sold_articles_view");}
    public void ordersView() { printBundleStringWithExit("orders_view");}

    public void timeMachineDate() { printBundleString("time_enter_date"); }

    public void orderSelect() { printBundleStringWithExit("order_select"); }
    public void cannotCancelOrReturn() { printBundleString("order_cannot_cancel_or_return"); }

    public void cannotReturnOrder() { printBundleString("order_limit_exceed_to_return");}
    public void cartOptions() {
        String sb = "\n" +
                "1 - " + this.bundle.getString("cart_remove") + "\n" +
                "2 - " + this.bundle.getString("cart_make_order");
        printString(sb);
    }

    public void orderCancel() {
        String sb = "1 - " + this.bundle.getString("order_cancel") + "\n" +
                "0 - " + this.bundle.getString("order_exit");
        printString(sb);
    }
    public void orderReturn() {
        String sb = "1 - " + this.bundle.getString("order_return") + "\n" +
                "0 - " + this.bundle.getString("order_exit");
        printString(sb);
    }

    public void userMode() {
        String sb = "\n" +
                this.bundle.getString("user_mode_header") + "\n" +
                "1 - " + this.bundle.getString("user_mode_sell") + "\n" +
                "2 - " + this.bundle.getString("user_mode_view_articles") + "\n" +
                "3 - " + this.bundle.getString("user_mode_view_cart") + "\n" +
                "4 - " + this.bundle.getString("user_mode_view_sold_articles") + "\n" +
                "5 - " + this.bundle.getString("user_mode_view_for_sale_articles") + "\n" +
                "6 - " + this.bundle.getString("user_mode_orders") + "\n" +
                "7 - " + this.bundle.getString("user_mode_time") + "\n" +
                // "8 - " + this.bundle.getString("user_mode_account") + "\n" +
                "0 - " + this.bundle.getString("user_mode_back") + "\n";
        printString(sb);
    }

    public void typeOfArticle() {
        String sb = "\n" +
                this.bundle.getString("article_type_select") + "\n" +
                "1 - " + this.bundle.getString("article_type_shirt") + "\n" +
                "2 - " + this.bundle.getString("article_type_bag") + "\n" +
                "3 - " + this.bundle.getString("article_type_shoes") + "\n";
        printString(sb);
    }

    public void articleBehaviour() {
        String sb = "\n" +
                this.bundle.getString("article_behaviour_select") + "\n" +
                "1 - " + this.bundle.getString("article_behaviour_used") + "\n" +
                "2 - " + this.bundle.getString("article_behaviour_premium") + "\n" +
                "3 - " + this.bundle.getString("article_behaviour_used_premium") + "\n" +
                "4 - " + this.bundle.getString("article_behaviour_none") + "\n";
        printString(sb);
    }

        public void printCarrierNamesList (List<String> names) {
        int iteration = 1;
        StringBuilder sb = new StringBuilder();
        sb.append("Select a carrier: \n");
        for (String name : names) {
            sb.append(iteration).append(" - ");
            sb.append(name);
            sb.append("\n");
            iteration++;
        }
        printString(sb.toString());
    }

    public void displayList(List<String> names) {
        int iteration = 1;
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append("\n[").append(iteration).append("] : ");
            sb.append(name);
            iteration++;
        }
        printString(sb.toString());
    }

    public void displayTotalPrice (String str, double price) {
        String formattedPrice = String.format("%.2f", price);
        this.printString(str + " total price: " + formattedPrice + "$");
    }

    public void printBundleString (String str) {
        printString(this.bundle.getString(str));
    }

    public void printBundleStringWithExit (String str) {
        printString(this.bundle.getString(str) + " " + this.bundle.getString("exit_prompt"));
    }

}
