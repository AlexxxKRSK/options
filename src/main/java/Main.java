import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static List<Option> now = new ArrayList<>();
    static List<Option> previous = new ArrayList<>();
    static List<Option> previousDay = new ArrayList<>();
    static List<Record> ri = new ArrayList<>();
    static List<Record> riDay = new ArrayList<>();
    static List<Record> others = new ArrayList<>();
    static List<Record> othersDay = new ArrayList<>();
    static String url;
    static String pass;
    static String user;
    static String sourceFolder;
    static Path folder;
    static Path file;
    static FileTime fileTime = FileTime.fromMillis(0);
    static String newTableNow;

    static {
        var rb = ResourceBundle.getBundle("Properties");
        url = rb.getString("url");
        pass = rb.getString("pass");
        user = rb.getString("user");
        newTableNow = rb.getString("newTableNow");
        sourceFolder = rb.getString("sourceFolder");
        folder = Path.of(sourceFolder);
        file = Path.of(sourceFolder);
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
//          Get entry<fileTimeUpdate:fileName> for most fresh file
            var dt = getLatestFile(folder);
//          On new update start analyzing
            if(dt.getKey().toMillis()>fileTime.toMillis() ) {
                fileTime = dt.getKey();
                System.out.println(dt.getKey() + "  ***  " + dt.getValue());
//                readFile(file);
//                updateDB("DROP TABLE IF EXISTS previous;" +
//                        "CREATE TABLE previous AS TABLE now;");
//                writeDB(now, "now");
                checkDB(ri, others);
                others.forEach(System.out::println);
//                readDB("SELECT COUNT(*) FROM now");

//                If new file created
//                if(!dt.getValue().equals(file) & !dt.getValue().toString().equals(sourceFolder)){
//                    file = dt.getValue();
//                    System.out.println("New File found=" + file);
//                    previousDay=new ArrayList<>(previous);
//                    writeDB(previousDay,"previousDay");
//                }

            }
            Thread.sleep(5000);
        }
    }

    private static FileTime getFileTime(Path p) {
        try {
            return Files.getLastModifiedTime(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map.Entry<FileTime, Path> getLatestFile(Path source){
        try (Stream<Path> s = Files.walk(source)) {
            return s
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toMap(Main::getFileTime, v->v))
                    .entrySet().stream()
                    .reduce((e1,e2)-> e1.getKey().toMillis()>e2.getKey().toMillis()?e1:e2).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void readDB(String sql) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)){
            var statement = conn.prepareStatement(sql);
            var rs = statement.executeQuery();
            while (rs.next())
                System.out.println(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void checkDB(List<Record> riList, List<Record> othersList) {
        riList.clear();
        othersList.clear();
//        Select new deals greater then 1 mln RUB and insert into "ri" table
        String sql = "SELECT now.date, code, now.base, now.type, now.strike, now.expiry, " +
                "(now.numberofcontracts-previous.numberofcontracts)*now.theoreticalprice/1000000 as prcntChange, " +
                "CASE WHEN now.type = 'Call' THEN (now.strike + now.theoreticalPrice) ELSE (now.strike - now.theoreticalPrice) END as level," +
                " now.theoreticalPrice, now.numberofcontracts as oiNow, previous.numberofcontracts as oiPrev, " +
                "(now.numberofcontracts - previous.numberofcontracts)/ now.numberofcontracts*100 as oiChange " +
                "FROM now JOIN previous USING (code) WHERE (now.numberofcontracts!=previous.numberofcontracts) " +
                "AND (now.numberofcontracts-previous.numberofcontracts)*now.theoreticalprice/1000000>=1;";

        try (Connection conn = DriverManager.getConnection(url, user, pass)){
            var statement = conn.prepareStatement(sql);
            var rs = statement.executeQuery();
            while (rs.next()){
                if (rs.getString("code").startsWith("RI"))
                    riList.add(new Record(rs));
                else othersList.add(new Record(rs));
           }
            riList.forEach(x -> System.out.println("RI=" + x));
            othersList.forEach(x -> System.out.println("Other=" + x));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void updateDB(String sql) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)){
            var statement = conn.prepareStatement(sql);
            var rs = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void writeDB(List<Option> list, String table) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)){
            String sql = "TRUNCATE " + table + ";" +
                    "INSERT INTO " + table + " VALUES (?,?,?,?,?, ?,?,?,?,?, ?,?,?) ON CONFLICT DO NOTHING";
            for (Option opt: list){
                var statement = conn.prepareStatement(sql);
                statement.setDate(1, Date.valueOf(opt.getDt()));
                statement.setString(2, opt.getCode());
                statement.setDate(3, Date.valueOf(opt.getExpiry()));
                statement.setString(4, opt.getType());
                statement.setInt(5, opt.getStrike());
                statement.setString(6, opt.getBase());
                statement.setDouble(7, opt.getTheoreticalPrice());
                statement.setDouble(8, opt.getPriceHi());
                statement.setDouble(9, opt.getPriceLo());
                statement.setInt(10, opt.getNumberOfContracts());
                statement.setInt(11, opt.getNumberOfTrades());
                statement.setInt(12, opt.getVolumeToday());
                statement.setDouble(13, opt.getValueToday());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void readFile(Path path){
        now.clear();
        try (var stream = Files.lines(path)) {
            stream.skip(1)
//                    .limit(100)
                    .map(s-> Arrays.asList(s.split(";")))
                    .forEach(l->now.add(new Option(l)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
