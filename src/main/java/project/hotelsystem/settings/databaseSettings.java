package project.hotelsystem.settings;

import java.util.prefs.Preferences;

/**
 * @author Nomar
 */

public class databaseSettings {

    public static databaseSettings instance;

    private static final String PREFERENCE_NODE_NAME = "hotel.system.database";
    private static final String LOCAL_URL_KEY = "url_local";
    private static final String LOCAL_USER_KEY = "user_local";
    private static final String LOCAL_PASSWORD_KEY = "password_local";

    private String local_url = "jdbc:mysql://localhost:3306/";
    private String local_user = "root";
    private String local_password;

    private static final String CLOUD_URL_KEY = "url_cloud";
    private static final String CLOUD_USER_KEY = "user_cloud";
    private static final String CLOUD_PASSWORD_KEY = "password_cloud";

    private String cloud_url;
    private String cloud_user;
    private String cloud_password;

    public static databaseSettings getInstance() {
        if (instance == null) {
            instance = new databaseSettings();
        }
        return instance;
    }

    public String getLocal_url() {
        return local_url;
    }

    public void setLocal_url(String local_url) {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        this.local_url = local_url;
        preferences.put(LOCAL_URL_KEY, local_url);

    }

    public String getLocal_user() {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        return local_user;
    }

    public void setLocal_user(String local_user) {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        this.local_user = local_user;
        preferences.put(LOCAL_USER_KEY, local_user);

    }

    public String getLocal_password() {
        return local_password;
    }

    public void setLocal_password(String local_password) {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        this.local_password = local_password;
        preferences.put(LOCAL_PASSWORD_KEY, local_password);
    }

    public String getCloud_url() {
        return cloud_url;
    }

    public void setCloud_url(String cloud_url) {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        this.cloud_url = cloud_url;
        preferences.put(CLOUD_URL_KEY, cloud_url);

    }

    public String getCloud_user() {
        return cloud_user;
    }

    public void setCloud_user(String cloud_user) {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        this.cloud_user = cloud_user;
        preferences.put(CLOUD_USER_KEY, cloud_user);
    }

    public String getCloud_password() {
        return cloud_password;
    }

    public void setCloud_password(String cloud_password) {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        this.cloud_password = cloud_password;
        preferences.put(CLOUD_PASSWORD_KEY, cloud_password);
    }

    public void loadSettings() {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        this.local_url = preferences.get(LOCAL_URL_KEY, "jdbc:mysql://localhost:3306/defaultDB");
        this.local_user = preferences.get(LOCAL_USER_KEY, local_user);
        this.local_password = preferences.get(LOCAL_PASSWORD_KEY, "");

        this.cloud_url = preferences.get(CLOUD_URL_KEY, "jdbc:mysql://cloud-host:3306/defaultDB");
        this.cloud_user = preferences.get(CLOUD_USER_KEY, "");
        this.cloud_password = preferences.get(CLOUD_PASSWORD_KEY, "");
    }

    public void saveSettings() {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        preferences.put(LOCAL_URL_KEY, local_url);
        preferences.put(LOCAL_USER_KEY, local_user);
        preferences.put(LOCAL_PASSWORD_KEY, local_password);

        preferences.put(CLOUD_URL_KEY, cloud_url);
        preferences.put(CLOUD_USER_KEY, cloud_user);
        preferences.put(CLOUD_PASSWORD_KEY, cloud_password);
    }

    public void saveLocalSettings(String url, String user, String pw) {
        Preferences preferences = Preferences.userRoot().node(PREFERENCE_NODE_NAME);

        this.local_url = url;
        this.local_user = user;
        this.local_password = pw;

        preferences.put(LOCAL_URL_KEY, local_url);
        preferences.put(LOCAL_USER_KEY, local_user);
        preferences.put(LOCAL_PASSWORD_KEY, local_password);

    }


}
