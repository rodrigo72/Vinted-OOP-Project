package Logs;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppLogger {
    private static final String ERROR_LOGS_FILE_NAME = "./src/Logs/error.log";
    public static void logError (Exception e) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ERROR_LOGS_FILE_NAME, true))) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String timestamp = sdf.format(new Date());

            pw.println(timestamp + ":");
            e.printStackTrace(pw);
            pw.println();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static final String REQUEST_LOGS_FILE_NAME = "simple.log";
    public static void logRequest (String message) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(REQUEST_LOGS_FILE_NAME, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String timestamp = sdf.format(new Date());
            pw.println(timestamp + ": " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
