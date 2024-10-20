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
import java.io.FileOutputStream;
import java.io.IOException;
import project.hotelsystem.util.invoiceManager;

public class invoiceSettings

{
    String dest = "/Users/thantzinlin/Desktop/invoice.pdf";

    public void openPdfModal(String id) {
        System.out.println("hello");

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);

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

        Button saveBtn = new Button("save");
        Button printBtn = new Button("print");

        ComboBox<PrintService> printers = new ComboBox<>();
        PrintService[] pr = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService p : pr) {
            printers.getItems().add(p);
        }
        printers.getSelectionModel().selectFirst();

        HBox controlsBox = new HBox(saveBtn,printers,printBtn);

        saveBtn.setOnAction(e->{
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
