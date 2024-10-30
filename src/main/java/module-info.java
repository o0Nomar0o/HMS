module project.hotelsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires password4j;
    requires java.prefs;
    requires bouncy.castle.connector;
    requires kernel;
    requires pdfa;
    requires layout;
    requires io;
    requires pdfua;
    requires java.desktop;
    requires org.apache.pdfbox.io;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires commons;
    requires Java.WebSocket;
    requires redis.clients.jedis;


    opens project.hotelsystem to javafx.fxml;
    exports project.hotelsystem;

    opens project.hotelsystem.controller to javafx.fxml;
    exports project.hotelsystem.controller;

    opens project.hotelsystem.util to javafx.fxml;
    exports project.hotelsystem.util;

    opens project.hotelsystem.database.models to javafx.fxml;
    exports project.hotelsystem.database.models;

}