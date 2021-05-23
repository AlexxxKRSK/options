import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    static List<Option> now = new ArrayList<>();
    static List<Option> previous = null;
    static List<Option> previousDay =null;
    static List<Record> ri = new ArrayList<>();
    static List<Record> riDay = new ArrayList<>();
    static List<Record> others = new ArrayList<>();
    static List<Record> othersDay = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException, IOException {
        Props props = Props.getProps();
        FileHelper fileHelper = new FileHelper();
        while (true) {
//          Get entry<fileTimeUpdate:fileName> for most fresh file
            Map.Entry<FileTime,Path> latestFile = fileHelper.getLatestFile(props.getSourceFolder());
//          On new update start analyzing, check file size as well
            if(latestFile.getKey().toMillis()> props.getLastFileTime().toMillis() && Files.size(latestFile.getValue())>200_000) {
                System.out.println(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(latestFile.getKey().toMillis()) + "  ***  "
                        + latestFile.getValue().getFileName());
                previous = new ArrayList<>(now);
//              Read new file to 'now' ArrayList
                fileHelper.readFile(latestFile.getValue(), now);
                try (Connection conn = DriverManager.getConnection(props.getUrl(), props.getUser(), props.getPass())){
                    conn.setAutoCommit(false);
//                  Write 'now' to DB
                    String pushNowIntoPrevious =
                                    "DROP TABLE IF EXISTS previous; " +
                                    "CREATE TABLE previous AS TABLE now; " +
                                    "TRUNCATE now;";
                    PreparedStatement statement = conn.prepareStatement(pushNowIntoPrevious);
                    statement.executeUpdate();
                    now.parallelStream().forEach(o-> o.pushToDB(conn));
                    conn.commit();
//      Check for new deals greater 1 mln
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
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()){
                        if (rs.getString("code").startsWith("RI"))
                            ri.add(new Record(rs));
                        else others.add(new Record(rs));
                    }

                    StringBuilder mail = new StringBuilder("Base\texpiry\ttype\tstrike\tmoneyChange\tlevel");
                    ri.forEach(r->{r.pushToDB(conn,"ri");
                        System.out.println(r);
                        if(r.getMoneyChange() > 5) mail.append("\n" + r.toMailString());});
                    others.forEach(r->{r.pushToDB(conn, "others");
                        System.out.println(r);
                        if(r.getMoneyChange() > 5) mail.append("\n").append(r.toMailString());});
                    conn.commit();
                    if(mail.length()>42) {
                        SendEMail.send("5 min report", mail.toString());
                    }

//      On new file found
                    if(!latestFile.getValue().equals(props.getLastFile())){
                        System.out.println("New File found=" + latestFile.getValue());
//                      Clear 'riDay' and 'othersDay' lists
                        riDay.clear();
                        othersDay.clear();
//                      Select new deals greater then 10 mln RUB and insert into "riDay" and "othersDay" tables
                        String checkDay =
                                "SELECT previous.date, code, previous.base, previous.type, previous.strike, previous.expiry, " +
                                "(previous.open_interest-previous_day.open_interest)*previous.theoretical_price/1000000 as money_change, " +
                                "CASE WHEN previous.type = 'Call' THEN (previous.strike + previous.theoretical_price) ELSE (previous.strike - previous.theoretical_price) END as level," +
                                " previous.theoretical_price, previous.open_interest as oiNow, previous_day.open_interest as oi_prev, " +
                                "(previous.open_interest - previous_day.open_interest)/ previous.open_interest*100 as oi_change " +
                                "FROM previous JOIN previous_day USING (code) WHERE (previous.open_interest!=previous_day.open_interest) " +
                                "AND ABS((previous.open_interest-previous_day.open_interest)*previous.theoretical_price/1000000)>10;";
                        statement = conn.prepareStatement(checkDay);
                        rs = statement.executeQuery();
                        while (rs.next()){
                            if (rs.getString("code").startsWith("RI"))
                                riDay.add(new Record(rs));
                            else othersDay.add(new Record(rs));
                        }

                        StringBuilder mailDay = new StringBuilder("Base\texpiry\ttype\tstrike\tmoneyChange\tlevel");
                        riDay.forEach(r->{r.pushToDB(conn, "ri_day");
                                        System.out.println(r);
                                        mailDay.append("\n" + r.toMailString());});
                        othersDay.forEach(r->{r.pushToDB(conn, "others_day");
                                        System.out.println(r);
                                        mailDay.append("\n").append(r.toMailString());});

                        if(mailDay.length()>42) {
                            SendEMail.send("Daily report", mailDay.toString());
                        }

                        previousDay=new ArrayList<>(previous);

                        String dateForArchive = "SELECT DISTINCT date FROM previous_day;";
                        statement = conn.prepareStatement(dateForArchive);
                        rs = statement.executeQuery();
                        conn.commit();
                        String tableDate=null;
                        while (rs.next()){
                            tableDate=rs.getString("date");
                        }

                        String onNewFile =
                                "ALTER TABLE previous_day RENAME TO \"" + tableDate + "\";" +
                                "DROP TABLE IF EXISTS previous_day;" +
                                "TRUNCATE ri; " +
                                "TRUNCATE others;" +
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
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.getLogger().logIt(e);
                }
//              Update 'lastFile' and 'lastFileTime' with new values
                props.setLastFile(latestFile.getValue());
                System.out.println("Waiting for update");
            }
            Thread.sleep(props.getTimeOut());
        }
    }
}
