module dev.bong {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires ivy.java;


    exports dev.bong.control;
    opens dev.bong.control to javafx.fxml;
    exports dev.bong.view;
    opens dev.bong.view to javafx.fxml;


}