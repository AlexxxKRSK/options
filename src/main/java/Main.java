import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Main {
    static List<Option> now = new ArrayList<>();
    static List<Option> previous = null;
    static List<Option> previousDay =null;
    static List<Record> ri = new ArrayList<>();
    static List<Record> riDay = new ArrayList<>();
    static List<Record> others = new ArrayList<>();
    static List<Record> othersDay = new ArrayList<>();
    static String url;
    static String pass;
    static String user;
    static String sourceFolder;
    static Path folder;
    static Path lastFile;
    static FileTime lastFileTime = FileTime.fromMillis(0);
    static String newTableNow;

    static {
        var rb = ResourceBundle.getBundle("Properties");
        url = rb.getString("url");
        pass = rb.getString("pass");
        user = rb.getString("user");
        newTableNow = rb.getString("newTableNow");
        sourceFolder = rb.getString("sourceFolder");
        folder = Path.of(sourceFolder);
        lastFile = Path.of(rb.getString("lastFile"));
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        while (true) {
//          Get entry<fileTimeUpdate:fileName> for most fresh file
            var fileHelper = new FileHelper();
            var latestFile = fileHelper.getLatestFile(folder);
//          On new update start analyzing, check file size as well
            if(latestFile.getKey().toMillis()> lastFileTime.toMillis() && Files.size(latestFile.getValue())>200_000) {
                System.out.println(latestFile.getKey() + "  ***  " + latestFile.getValue());
                previous = new ArrayList<>(now);
//              Read new file to 'now' ArrayList
                fileHelper.readFile(latestFile.getValue(), now);

                boolean newFile = !latestFile.getValue().equals(lastFile) & !latestFile.getValue().toString().equals(sourceFolder);

                try (Connection conn = DriverManager.getConnection(url, user, pass)){
                    conn.setAutoCommit(false);
//                  Write 'now' to DB
                    String pushNowIntoPrevious =
                                    "DROP TABLE IF EXISTS previous; " +
                                    "CREATE TABLE previous AS TABLE now; " +
                                    "TRUNCATE now;";
                    var statement = conn.prepareStatement(pushNowIntoPrevious);
                    statement.executeUpdate();
                    System.out.println(60);
                    now.parallelStream().forEach(o-> o.pushToDB(conn));
                    conn.commit();

//              Check for new deals greater 1 mln
//                  Clear 'ri' and 'others' lists
                    ri.clear();
                    others.clear();
//                  Select new deals greater then 1 mln RUB and insert into "ri" and "others" tables
                    String check =
                            "SELECT now.date, code, now.base, now.type, now.strike, now.expiry, " +
                            "(now.open_interest-previous.open_interest)*now.theoretical_price/1000000 as money_change, " +
                            "CASE WHEN now.type = 'Call' THEN (now.strike + now.theoretical_price) ELSE (now.strike - now.theoretical_price) END as level," +
                            " now.theoretical_price, now.open_interest as oiNow, previous.open_interest as oi_prev, " +
                            "(now.open_interest - previous.open_interest)/ now.open_interest*100 as oi_change " +
                            "FROM now JOIN previous USING (code) WHERE (now.open_interest!=previous.open_interest) " +
                            "AND ((now.open_interest-previous.open_interest)*now.theoretical_price/1000000) NOT BETWEEN -1 AND 1;";
                    statement = conn.prepareStatement(check);
                    var rs = statement.executeQuery();
                    while (rs.next()){
                        if (rs.getString("code").startsWith("RI"))
                            ri.add(new Record(rs));
                        else others.add(new Record(rs));
                    }
                    ri.forEach(x -> System.out.println("RI=" + x));
                    others.forEach(x -> System.out.println("Other=" + x));

                    ri.forEach(r->r.pushToDB(conn,"ri"));
                    others.forEach(r->r.pushToDB(conn, "others"));
                    conn.commit();
                    System.out.println(89);

//              On new file found
                    if(newFile){
                        System.out.println("New File found=" + latestFile.getValue());

//                      Clear 'riDay' and 'othersDay' lists
                        riDay.clear();
                        othersDay.clear();
//                      Select new deals greater then 10 mln RUB and insert into "riDay" and "othersDay" tables
                        String checkDay =
                                "SELECT now.date, code, now.base, now.type, now.strike, now.expiry, " +
                                "(now.open_interest-previous_day.open_interest)*now.theoretical_price/1000000 as money_change, " +
                                "CASE WHEN now.type = 'Call' THEN (now.strike + now.theoretical_price) ELSE (now.strike - now.theoretical_price) END as level," +
                                " now.theoretical_price, now.open_interest as oiNow, previous_day.open_interest as oi_prev, " +
                                "(now.open_interest - previous_day.open_interest)/ now.open_interest*100 as oi_change " +
                                "FROM now JOIN previous_day USING (code) WHERE (now.open_interest!=previous_day.open_interest) " +
                                "AND ABS((now.open_interest-previous_day.open_interest)*now.theoretical_price/1000000)>10;";
                        statement = conn.prepareStatement(checkDay);
                        rs = statement.executeQuery();
                        while (rs.next()){
                            if (rs.getString("code").startsWith("RI"))
                                riDay.add(new Record(rs));
                            else othersDay.add(new Record(rs));
                        }
                        riDay.forEach(x -> System.out.println("riDay=" + x));
                        othersDay.forEach(x -> System.out.println("otherDay=" + x));

                        riDay.forEach(r->r.pushToDB(conn, "ri_day"));
                        othersDay.forEach(r->r.pushToDB(conn, "others_day"));

                        previousDay=new ArrayList<>(previous);
                        String onNewFile =
                                "TRUNCATE ri; " +
                                "TRUNCATE others;" +
                                "DROP TABLE IF EXISTS previous_day;" +
                                "CREATE TABLE previous_day AS TABLE previous;" +
                                "WITH to_archive_ri AS (DELETE FROM ri_day WHERE expiry < NOW() RETURNING *)" +
                                "INSERT INTO archive_ri " +
                                "SELECT * FROM to_archive_ri;" +
                                "WITH to_archive_others AS (DELETE FROM others_day WHERE expiry < NOW() RETURNING *)" +
                                "INSERT INTO archive_others " +
                                "SELECT * FROM to_archive_others;";
                        statement = conn.prepareStatement(onNewFile);
                        statement.executeUpdate();
                        conn.commit();
                }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
//              Update 'lastFile' and 'lastFileTime' with new values
                lastFile = latestFile.getValue();
                lastFileTime = latestFile.getKey();
            }
            Thread.sleep(5000);
        }
    }

}
