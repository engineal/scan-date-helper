module com.engineal.scandatehelper {
    requires java.prefs;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.imaging;

    opens com.engineal.scandatehelper to javafx.fxml;

    opens com.engineal.scandatehelper.control to javafx.fxml;
    opens com.engineal.scandatehelper.model to javafx.fxml;
    exports com.engineal.scandatehelper;
}
