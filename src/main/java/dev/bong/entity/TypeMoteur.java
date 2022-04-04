package dev.bong.entity;

public enum TypeMoteur {
    BINGBONG,BONGALA,INTERSECTION,UNION;

    public static TypeMoteur typeDeMoteur(String type){
        if (type.equals("BINGBONG")){
            return BINGBONG;
        } else if (type.equals("BONGALA")){
            return BONGALA;
        } else if (type.equals("INTERSECTION")){
            return INTERSECTION;
        } else {
            return UNION;
        }
    }
}
