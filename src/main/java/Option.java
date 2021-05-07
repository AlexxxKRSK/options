import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Option {
//    date date NOT NULL,
//    code character varying NOT NULL,
//    expiry date NOT NULL,
//    type character varying NOT NULL,
//    strike integer NOT NULL,
//
//    base character varying NOT NULL,
//    theoreticalPrice double precision NOT NULL,
//    priceHi double precision NOT NULL,
//    priceLo double precision NOT NULL,
//    numberOfContracts integer NOT NULL,
//
//    numberOfTrades integer NOT NULL,
//    volumeToday integer NOT NULL,
//    valueToday integer NOT NULL,

    LocalDate dt;
    String code;
    LocalDate expiry;
    String type;
    Integer strike;
    String base;
    Double theoreticalPrice;
    Double priceHi;
    Double priceLo;
    Integer numberOfContracts;
    Integer numberOfTrades;
    Integer volumeToday;
    Double valueToday;

    public void setDt(LocalDate dt) {
        this.dt = dt;
    }

    public Option() {
    }

    public Option(List<String> ls) {
        dt = LocalDate.parse(ls.get(0), DateTimeFormatter.ofPattern("yyyyMMdd"));
        code = ls.get(1);
        expiry = LocalDate.parse(ls.get(2), DateTimeFormatter.ofPattern("yyyyMMdd"));
        type= ls.get(3);
        strike = Integer.parseInt(ls.get(4));
        base = ls.get(5);
        theoreticalPrice = Double.parseDouble(ls.get(6).replace(',','.'));
        priceHi = Double.parseDouble(ls.get(7).replace(',','.'));
        priceLo =Double.parseDouble(ls.get(8).replace(',','.'));
        numberOfContracts = Integer.parseInt(ls.get(9));
        numberOfTrades = Integer.parseInt(ls.get(10));
        volumeToday = Integer.parseInt(ls.get(11));
        valueToday = Double.parseDouble(ls.get(12).replace(',','.'));
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setExpiry(LocalDate expiry) {
        this.expiry = expiry;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStrike(Integer strike) {
        this.strike = strike;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setTheoreticalPrice(Double theoreticalPrice) {
        this.theoreticalPrice = theoreticalPrice;
    }

    public void setPriceHi(Double priceHi) {
        this.priceHi = priceHi;
    }

    public void setPriceLo(Double priceLo) {
        this.priceLo = priceLo;
    }

    public void setNumberOfContracts(Integer numberOfContracts) {
        this.numberOfContracts = numberOfContracts;
    }

    public void setNumberOfTrades(Integer numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public void setVolumeToday(Integer volumeToday) {
        this.volumeToday = volumeToday;
    }

    public void setValueToday(Double valueToday) {
        this.valueToday = valueToday;
    }

    public LocalDate getDt() {
        return dt;
    }

    public String getCode() {
        return code;
    }

    public LocalDate getExpiry() {
        return expiry;
    }

    public String getType() {
        return type;
    }

    public Integer getStrike() {
        return strike;
    }

    public String getBase() {
        return base;
    }

    public Double getTheoreticalPrice() {
        return theoreticalPrice;
    }

    public Double getPriceHi() {
        return priceHi;
    }

    public Double getPriceLo() {
        return priceLo;
    }

    public Integer getNumberOfContracts() {
        return numberOfContracts;
    }

    public Integer getNumberOfTrades() {
        return numberOfTrades;
    }

    public Integer getVolumeToday() {
        return volumeToday;
    }

    public Double getValueToday() {
        return valueToday;
    }

    @Override
    public String toString() {
        return "Option{" +
                "dt=" + dt +
                ", code='" + code + '\'' +
                ", expiry=" + expiry +
                ", type='" + type + '\'' +
                ", strike=" + strike +
                ", base='" + base + '\'' +
                ", theoreticalPrice=" + theoreticalPrice +
                ", priceHi=" + priceHi +
                ", priceLo=" + priceLo +
                ", numberOfContracts=" + numberOfContracts +
                ", numberOfTrades=" + numberOfTrades +
                ", volumeToday=" + volumeToday +
                ", valueToday=" + valueToday +
                '}';
    }

}
