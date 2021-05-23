import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class for new update record
 */
public class Record {
//    date date NOT NULL,
//    code character varying NOT NULL,
//    base character varying NOT NULL,
//    type character varying NOT NULL,
//    strike integer NOT NULL,
//
//    expiry date NOT NULL,
//    changeMoney double precision NOT NULL,
//    level double precision NOT NULL,
//    theoreticalPrice double precision NOT NULL,
//    openInterest double precision NOT NULL,
//
//    openInterestPrevious double precision NOT NULL,
//    openInterestChange double precision NOT NULL,

    LocalDateTime time;
    String code;
    String base;
    String type;
    Integer strike;

    LocalDate expiry;
    Double moneyChange;
    Double level;
    Double theoreticalPrice;
    Double openInterest;

    Double openInterestPrevious;
    Double openInterestChange;


    @Override
    public String toString() {
        return "Record{" +
                "code=" + code +
                ", expiry=" + expiry +
                ", base=" + base +
                ", type=" + type +
                ", strike=" + strike +
                ", moneyChange=" + Math.round(moneyChange) +
                ", level=" + Math.round(level) +
                ", openInterestChange=" + Math.round(openInterestChange) +
                '}';
    }
    public String toMailString() {
        return "{" + base +
                "\t" + expiry +
                "\t" + type +
                "\t" + strike +
                "\t" + Math.round(moneyChange) +
                "\t" + Math.round(level) + '}';
    }

    public Record(ResultSet rs) throws SQLException {
        time = LocalDateTime.now();
        code = rs.getString(2);
        base = rs.getString(3);
        type= rs.getString(4);
        strike = rs.getInt(5);

        expiry = rs.getDate(6).toLocalDate();
        moneyChange = rs.getDouble(7);
        level = rs.getDouble(8);
        theoreticalPrice =rs.getDouble(9);
        openInterest = rs.getDouble(10);

        openInterestPrevious = rs.getDouble(11);
        openInterestChange = rs.getDouble(12);
    }

    public void pushToDB(Connection conn, String table) {
        String sql = "INSERT INTO "+ table + " VALUES (?,?,?,?,?, ?,?,?,?,?, ?,?);"; //'" ON CONFLICT DO NOTHING;";
        try (PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setObject(1,this.getTime());
            statement.setString(2, this.getCode());
            statement.setString(3, this.getBase());
            statement.setString(4, this.getType());
            statement.setInt(5, this.getStrike());

            statement.setDate(6, Date.valueOf(this.getExpiry()));
            statement.setDouble(7, this.getMoneyChange());
            statement.setDouble(8, this.getLevel());
            statement.setDouble(9, this.getTheoreticalPrice());
            statement.setDouble(10, this.getOpenInterest());

            statement.setDouble(11, this.getOpenInterestPrevious());
            statement.setDouble(12, this.getOpenInterestChange());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger().logIt(e);
        }
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getCode() {
        return code;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStrike() {
        return strike;
    }

    public void setStrike(Integer strike) {
        this.strike = strike;
    }

    public LocalDate getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDate expiry) {
        this.expiry = expiry;
    }

    public Double getMoneyChange() {
        return moneyChange;
    }

       public Double getLevel() {
        return level;
    }

    public Double getTheoreticalPrice() {
        return theoreticalPrice;
    }

    public Double getOpenInterest() {
        return openInterest;
    }

    public Double getOpenInterestPrevious() {
        return openInterestPrevious;
    }

    public Double getOpenInterestChange() {
        return openInterestChange;
    }
}
