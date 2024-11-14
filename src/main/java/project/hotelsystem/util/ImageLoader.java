package project.hotelsystem.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.sql.Blob;

public class ImageLoader {
    public static Image loadImageFromBlob(Blob blob, double width, double height) {
        try {
            byte[] imgBytes = blob.getBytes(1, (int) blob.length());
            ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);

            return new Image(bais, width, height, true, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
