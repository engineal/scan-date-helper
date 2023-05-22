module com.engineal.scandatehelper {
    requires java.prefs;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.imaging;

    opens com.engineal.scandatehelper to javafx.fxml;
    opens com.engineal.scandatehelper.control to javafx.fxml;
    opens com.engineal.scandatehelper.model to javafx.base;

    exports com.engineal.scandatehelper;
}
