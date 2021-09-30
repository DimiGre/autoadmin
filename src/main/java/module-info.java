module org.example {
    requires javafx.controls;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpmime;
    requires com.google.gson;
    requires javafx.fxml;
    exports MVC;
    exports admin;
}
