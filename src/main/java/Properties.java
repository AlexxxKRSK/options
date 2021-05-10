import jfork.nproperty.Cfg;
import jfork.nproperty.ConfigParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class Properties {
    private static String url;
    private static String pass;
    private static String user;
    private static String newTableNow;
    private static String sourceFolder;
    private int test = 1;
    static File file = new File("property.properties");

    static void saveProperties(java.util.Properties p) throws IOException
    {
        FileOutputStream fr = new FileOutputStream(file);
        p.store(fr, "Properties");
        fr.close();
        System.out.println("After saving properties: " + p);
    }

    static void loadProperties(java.util.Properties p)throws IOException
    {
        FileInputStream fi=new FileInputStream(file);
        p.load(fi);
        fi.close();
        System.out.println("After Loading properties: " + p);
    }

    public static void main(String... args)throws IOException
    {
        java.util.Properties table = new java.util.Properties();
        loadProperties(table);
        table.setProperty("Shivam","Bane");
        table.setProperty("CS","Maverick");
        System.out.println("Properties has been set in HashTable: " + table);
        // saving the properties in file
        saveProperties(table);
        // changing the property
        table.setProperty("Shivam", "Swagger");
        System.out.println("After the change in HashTable: " + table);
        // saving the properties in file
        saveProperties(table);
        System.out.println(table.getProperty("CS"));
        // loading the saved properties
    }
}