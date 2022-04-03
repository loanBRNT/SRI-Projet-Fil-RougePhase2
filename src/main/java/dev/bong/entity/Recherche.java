package dev.bong.entity;

public class Recherche {

    private String date;
    private String type;
    private String search;
    private String bans;
    private String resultats;


    public Recherche(String date, String type, String search, String bans, String resultats) {
        this.date = date;
        this.type = type;
        this.search = search;
        this.bans = bans;
        this.resultats = resultats;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getBans() {
        return bans;
    }

    public void setBans(String bans) {
        this.bans = bans;
    }

    public String getResultats() {
        return resultats;
    }

    public void setResultats(String resultats) {
        this.resultats = resultats;
    }
}
