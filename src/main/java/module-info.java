module dev.bong {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires ivy.java;
    requires javafx.media;


    exports dev.bong.control;
    opens dev.bong.control to javafx.fxml, javafx.media;
    exports dev.bong.view;
    opens dev.bong.view to javafx.fxml;
    exports dev.bong.entity;
    opens dev.bong.entity to javafx.fxml;

}