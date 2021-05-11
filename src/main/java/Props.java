import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Properties;

public class Props {
    private static Props instance;
    private static final File file = new File("Properties.properties");
    private final Properties properties;
    private final String url;
    private final String user;
    private final String pass;
    private final Path sourceFolder;
    private Path lastFile;
    private FileTime lastFileTime;

    private Props() {
        properties = new Properties();
        loadProperties(properties);
        url = properties.getProperty("url");
        user = properties.getProperty("user");
        pass = properties.getProperty("pass");
        sourceFolder = Path.of(properties.getProperty("sourceFolder"));
        lastFile = Path.of(properties.getProperty("lastFile"));
        lastFileTime = FileTime.fromMillis(Long.parseLong(properties.getProperty("lastFileTime")));
        if (!Files.exists(lastFile)) {
            lastFile = sourceFolder;
            lastFileTime = FileTime.fromMillis(0);
        }

    }

    public static Props getProps(){
        if (instance == null)
            instance = new Props();
        return instance;
    }

    private void loadProperties(Properties p)
    {
        try (FileInputStream fis = new FileInputStream(file)){
            p.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveProperties(Properties p)
    {
        try (FileOutputStream fr = new FileOutputStream(file)) {
            p.store(fr, "Properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public Path getSourceFolder() {
        return sourceFolder;
    }

    public Path getLastFile() {
        return lastFile;
    }

    public FileTime getLastFileTime() {
        return lastFileTime;
    }

    public void setLastFile(Path lastFile) {
        try {
            this.lastFile = lastFile;
            this.lastFileTime = Files.getLastModifiedTime(this.lastFile);
            properties.setProperty("lastFile", lastFile.toString());
            properties.setProperty("lastFileTime", "" + lastFileTime.toMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveProperties(properties);
    }
}