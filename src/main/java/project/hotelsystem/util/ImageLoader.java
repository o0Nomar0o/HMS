package project.hotelsystem.util;

import javafx.scene.image.Image;
import project.hotelsystem.settings.databaseSettings;
import java.io.ByteArrayInputStream;
import java.sql.Blob;

public class ImageLoader {
    static databaseSettings dbs = databaseSettings.getInstance();
    public static Image loadImageFromBlob(Blob blob, double width, double height) {
        try {
            String type = dbs.getPerformance();
            if(type.matches("on")){
                blob.free();
                return null;
            }
            byte[] imgBytes = blob.getBytes(1, (int) blob.length());
            ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);
            return new Image(bais, width, height, true, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
