module com.engineal.scandatehelper {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.engineal.scandatehelper to javafx.fxml;
    exports com.engineal.scandatehelper;
}