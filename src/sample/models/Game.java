package sample.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Game {
    private Integer rozgrywkaId;
    private String zwyciezca;
    private String graczX;
    private String graczO;
    private LocalDateTime dataczasRozgrywki;
    private String formattedDateTime;

    public Integer getRozgrywkaId() {
        return rozgrywkaId;
    }

    public void setRozgrywkaId(Integer rozgrywkaId) {
        this.rozgrywkaId = rozgrywkaId;
    }

    public String getZwyciezca() {
        return zwyciezca;
    }

    public void setZwyciezca(String zwyciezca) {
        this.zwyciezca = zwyciezca;
    }

    public String getGraczX() {
        return graczX;
    }

    public void setGraczX(String graczX) {
        this.graczX = graczX;
    }

    public String getGraczO() {
        return graczO;
    }

    public void setGraczO(String graczO) {
        this.graczO = graczO;
    }

    public LocalDateTime getDataczasRozgrywki() {
        return dataczasRozgrywki;
    }

    public void setDataczasRozgrywki(LocalDateTime dataczasRozgrywki) {
        this.dataczasRozgrywki = dataczasRozgrywki;
    }

    public String getFormattedDateTime() {
        return formattedDateTime;
    }

    public Game(Integer rozgrywkaId, String zwyciezca, String graczX, String graczO, LocalDateTime dataczasRozgrywki) {
        this.rozgrywkaId = rozgrywkaId;
        this.zwyciezca = zwyciezca;
        this.graczX = graczX;
        this.graczO = graczO;
        this.dataczasRozgrywki = dataczasRozgrywki;
        this.formattedDateTime = dataczasRozgrywki.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public Game(String zwyciezca, String graczX, String graczO, LocalDateTime dataczasRozgrywki) {
        this.zwyciezca = zwyciezca;
        this.graczX = graczX;
        this.graczO = graczO;
        this.dataczasRozgrywki = dataczasRozgrywki;
        this.formattedDateTime = dataczasRozgrywki.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}