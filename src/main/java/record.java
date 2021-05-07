import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class record {
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

    public record() {
    }

    public record(List<String> ls) {
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

    public Double getOpenInterestChange() {
        return openInterestChange;
    }

    public void setOpenInterestChange(Double openInterestChange) {
        this.openInterestChange = openInterestChange;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

}
