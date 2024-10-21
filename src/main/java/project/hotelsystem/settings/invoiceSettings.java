package project.hotelsystem.settings;

import com.itextpdf.commons.utils.Base64;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import project.hotelsystem.util.invoiceManager;
import project.hotelsystem.settings.userSettings;

public class invoiceSettings


{

    userSettings sets = userSettings.getInstance();

    public void openPdfModal(String id, Stage owner) {

        System.out.println(sets.getInvoice_path());

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(owner);

        modalStage.initStyle(StageStyle.UTILITY);

        ScrollPane imageScroll = new ScrollPane();

        ImageView pdfImageView = new ImageView();

        PDDocument document = loadPDF(id);

        if (document == null) return;
        try{

            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bufferedImage = renderer.renderImageWithDPI(0,300); // 0 -> first page

            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

            pdfImageView.setImage(fxImage);
            pdfImageView.setFitWidth(750);
            pdfImageView.setPreserveRatio(true);

        }catch (Exception e){
            e.printStackTrace();
        }

        Button saveBtn = new Button("Proceed Check-out");
        saveBtn.setStyle("-fx-pref-height: 250px;"+
                "-fx-background-color: #DAF5F2; " +
                "-fx-text-fill: #333333; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-pref-height: 250px;"+
                        "-fx-background-color: #e0e0e0; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #aaaaaa; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-pref-height: 250px;"+
                        "-fx-background-color: #F6F5F2; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);"));

        Button printBtn = new Button("Print");
        printBtn.setStyle("-fx-pref-height: 250px;"+
                "-fx-background-color: #EAFAD2; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);");
        printBtn.setOnMouseEntered(e -> printBtn.setStyle("-fx-pref-height: 250px;"+
                "-fx-background-color: #e0e0e0; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #aaaaaa; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);"));
        printBtn.setOnMouseExited(e -> printBtn.setStyle("-fx-pref-height: 250px;"+
                "-fx-background-color: #F6F5F2; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);"));

        ComboBox<PrintService> printers = new ComboBox<>();
        printers.setStyle(
                "-fx-background-color: #EDEADA; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);");
        PrintService[] pr = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService p : pr) {
            printers.getItems().add(p);
        }
        printers.getSelectionModel().selectFirst();

        HBox controlsBox = new HBox(saveBtn,printers,printBtn);

        saveBtn.setOnAction(e->{
            String invoicePath = sets.getInvoice_path();

            if (invoicePath == null || invoicePath.isEmpty()) {
                invoicePath = System.getProperty("user.home") + File.separator + "Invoices";
            }else{
                invoicePath+=File.separator + "Invoices";
            }

            File invoiceDir = new File(invoicePath);
            if (!invoiceDir.exists()) {
                if (invoiceDir.mkdir()) {
                    System.out.println("Created directory: " + invoicePath);
                } else {
                    System.out.println("Failed to create directory: " + invoicePath);
                    return;
                }
            }

            String dest = invoiceDir + File.separator + id + "invoice.pdf";
            System.out.println(dest);

            try(FileOutputStream fos = new FileOutputStream(dest)){
                document.save(fos);
                document.close();
            }catch (IOException f){
                f.printStackTrace();
            }
        });

        printBtn.setOnAction(e->printPDF(document, printers.getSelectionModel().getSelectedItem()));

        imageScroll.setContent(pdfImageView);
        imageScroll.setFitToWidth(true);
        VBox modalContent = new VBox(controlsBox,imageScroll);
        Scene modalScene = new Scene(modalContent,750, 550);
        modalStage.setScene(modalScene);
        modalStage.setTitle("Invoice PDF Viewer");
        modalStage.show();
    }
    private PDDocument loadPDF(String id){
        try {

            invoiceManager invM = new invoiceManager();
            ByteArrayOutputStream pdfInMemory = invM.createInvoice(id);
            PDDocument document = Loader.loadPDF(pdfInMemory.toByteArray());
            return  document;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    private void printPDF(PDDocument document, PrintService printerChoice){

        PrinterJob job = PrinterJob.getPrinterJob();
        try{
            job.setPageable(new PDFPageable(document));
            job.setPrintService(printerChoice);
            job.print();
            document.close();
        }catch (Exception e){
            e.printStackTrace();
            job.cancel();
        }

    }
}
