module com.mycompany.app.threads {
    requires static lombok;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    opens com.mycompany.app.threads to javafx.fxml;
    exports com.mycompany.app.threads;
}

