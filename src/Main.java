import Controller.AppController;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AppController appController = new AppController(scanner);

        appController.runController();
        appController.menu();
    }
} 