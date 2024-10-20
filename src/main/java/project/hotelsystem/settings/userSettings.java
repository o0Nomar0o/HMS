package project.hotelsystem.settings;

import javafx.scene.Parent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;

public class userSettings {

    private static userSettings instance;

    private static final String PREFERENCE_NODE_NAME = "hotel.system.user";
    private static final String THEME_KEY = "theme";
    private static final String USER_KEY = "user";
    private static final String PRIVILEGE_KEY = "privilege";
    private static final String USERNAME_KEY = "username";

    private String theme = "light";
    private String uid;
    private String username;
    private String invoice_path;
    private String privilege;

    public userSettings() {
    }

    public static userSettings getInstance() {
        if (instance == null) {
            instance = new userSettings();
        }
        return instance;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
        saveTheme();
    }

    public void setUser(String uid, String privilege, String username) {
        this.uid = uid;
        this.username = username;
        this.privilege = privilege;
        loadWindowsSettings(uid, privilege);
    }

    public String getPrivilege() {
        return privilege;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getUid() {
        return uid;
    }

    public String getInvoice_path() {
        return invoice_path;
    }

    public void setInvoice_path(String invoice_path) {
        this.invoice_path = invoice_path;
    }

    public void loadWindowsSettings() {
        if (uid != null && !uid.isEmpty()) {
            Preferences userPref = Preferences.userRoot().node(PREFERENCE_NODE_NAME + "/" + uid);
            userPref.put(USER_KEY, uid);
            this.theme = userPref.get(THEME_KEY, "light");
        }
    }
    public void loadWindowsSettings(String uid, String privilege) {
        if (uid != null && !uid.isEmpty()) {
            Preferences userPref = Preferences.userRoot().node(PREFERENCE_NODE_NAME + "/" + uid);
            userPref.put(USER_KEY, uid);
            userPref.put(PRIVILEGE_KEY, privilege);
            this.theme = userPref.get(THEME_KEY, "light");
        }
    }

    public Preferences getNodePreference(){
        return Preferences.userRoot().node(PREFERENCE_NODE_NAME + "/" + uid);
    }

    public void saveTheme() {
        if (uid != null && !uid.isEmpty()) {
            Preferences userPref = Preferences.userRoot().node(PREFERENCE_NODE_NAME + "/" + uid);
            userPref.put(THEME_KEY, theme);

        }
    }

    public void applyTheme(Parent root, String baseFilename) {
        try {

            loadWindowsSettings();

            String cssPath = "src/main/resources/css/" + baseFilename;
            if (theme.equals("dark")) {
                cssPath += "dark.css";
            } else {
                cssPath += ".css";
            }

            URL appTheme = new File(cssPath).toURI().toURL();
            root.getStylesheets().clear();
            root.getStylesheets().add(appTheme.toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        this.uid = null;
        this.theme = "light";
        this.invoice_path = null;
        Preferences userPref = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        try {
            userPref.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
