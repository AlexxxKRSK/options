import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    LocalDate date;
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

    public Record() {
    }

    public Record(List<String> ls) {
        date = LocalDate.parse(ls.get(0), DateTimeFormatter.ofPattern("yyyyMMdd"));
        code = ls.get(1);
        base = ls.get(5);
        type= ls.get(3);
        strike = Integer.parseInt(ls.get(4));

        expiry = LocalDate.parse(ls.get(2), DateTimeFormatter.ofPattern("yyyyMMdd"));
        moneyChange = Double.parseDouble(ls.get(6).replace(',','.'));
        level = Double.parseDouble(ls.get(7).replace(',','.'));
        theoreticalPrice =Double.parseDouble(ls.get(8).replace(',','.'));
        openInterest = Double.parseDouble(ls.get(9));

        openInterestPrevious = Double.parseDouble(ls.get(12).replace(',','.'));
        openInterestChange = Double.parseDouble(ls.get(12).replace(',','.'));
    }

    @Override
    public String toString() {
        return "Record{" +
                "date=" + date +
                ", code='" + code + '\'' +
                ", base='" + base + '\'' +
                ", type='" + type + '\'' +
                ", strike=" + strike +
                ", expiry=" + expiry +
                ", moneyChange=" + moneyChange +
                ", level=" + level +
                ", theoreticalPrice=" + theoreticalPrice +
                ", openInterest=" + openInterest +
                ", openInterestPrevious=" + openInterestPrevious +
                ", openInterestChange=" + openInterestChange +
                '}';
    }

    public Record(ResultSet rs) throws SQLException {
        date = rs.getDate(1).toLocalDate();
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public void setMoneyChange(Double moneyChange) {
        this.moneyChange = moneyChange;
    }

    public Double getLevel() {
        return level;
    }

    public void setLevel(Double level) {
        this.level = level;
    }

    public Double getTheoreticalPrice() {
        return theoreticalPrice;
    }

    public void setTheoreticalPrice(Double theoreticalPrice) {
        this.theoreticalPrice = theoreticalPrice;
    }

    public Double getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(Double openInterest) {
        this.openInterest = openInterest;
    }

    public Double getOpenInterestPrevious() {
        return openInterestPrevious;
    }

    public void setOpenInterestPrevious(Double openInterestPrevious) {
        this.openInterestPrevious = openInterestPrevious;
    }

    public Double getOpenInterestChange() {
        return openInterestChange;
    }

    public void setOpenInterestChange(Double openInterestChange) {
        this.openInterestChange = openInterestChange;
    }
}