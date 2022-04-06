package dev.bong.entity;

public enum TypeFichier {
    XML,
    JPG,
    BMP,
    WAV;

    public static String enumToString(TypeFichier typeFichier){
        String extension;
        switch (typeFichier){
            case BMP -> extension = ".bmp";
            case XML -> extension = ".xml";
            case JPG -> extension = ".jpg";
            default -> extension = ".wav";
        }
        return extension;
    }

    public static TypeFichier stringToEnum(String extension){
        TypeFichier typeFichier;
        switch (extension) {
            case ".xml" -> typeFichier = XML;
            case ".bmp" -> typeFichier = BMP;
            case ".jpg" -> typeFichier = JPG;
            case ".wav" -> typeFichier = WAV;
            default -> typeFichier = null;
        }
        return typeFichier;
    }
}
