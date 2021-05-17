import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * My logger singleton class
 */
public class Logger {

    private static File logfile = new File("log.txt");
    private static Logger logger;

    private Logger() {
    }

    public static Logger getLogger() {
        if (logger == null)
            logger = new Logger();
        return logger;
    }

    public void logIt (Exception e){
        try (BufferedWriter bfw = new BufferedWriter(new FileWriter(logfile,true))){
            bfw.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss dd-MM-yyyy")) + " * " + e.toString() + "\n");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
