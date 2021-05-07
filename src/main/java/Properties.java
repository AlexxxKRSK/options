import jfork.nproperty.Cfg;
import jfork.nproperty.ConfigParser;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

@Cfg
public class Properties {
    private static String url;
    private static String pass;
    private static String user;
    private static String newTableNow;
    private static String sourceFolder;
    private int test = 1;

    public Properties() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException
    {
//        ConfigParser.parse(Properties.class, "Properties.properties");
        ConfigParser.parse(this, "Properties.properties");
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        var prop = new Properties();
        System.out.println(prop.test);
        var p =Path.of("");
        System.out.println(p.toAbsolutePath());
    }
}
