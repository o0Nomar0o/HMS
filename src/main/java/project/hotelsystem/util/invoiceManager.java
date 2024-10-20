package project.hotelsystem.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import project.hotelsystem.database.controller.bookingController;
import project.hotelsystem.database.controller.invoiceGeneratorController;
import project.hotelsystem.database.models.InvoiceData;
import project.hotelsystem.database.models.booking;
import project.hotelsystem.database.models.order_food;
import project.hotelsystem.database.models.order_service;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class invoiceManager {

    double total = 0;

    public ByteArrayOutputStream createInvoice(String bkID) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.add(createHeader("Somber Sunset Inn", "Kanazawa\nJapan", "hoteltky@gmail.com"));

        booking bk = bookingController.getBookingById(bkID);
        String guestName = bk.getGuest().getGuest_name();
        String check_in = bk.getCheck_in().toString();
        String check_out = LocalDate.now().toString();

        document.add(createCustomerDetails(bkID, guestName, check_in, check_out));

        document.add(new Paragraph("\n"));

        document.add(createItemizedTable(bkID));

        document.add(createTotalSection(total));

        document.add(createFooter("Thank you for staying with us!"));

        document.close();

        return byteArrayOutputStream;

    }

    public static Paragraph createHeader(String hotelName, String address, String contact) {
        Paragraph header = new Paragraph()
                .add(hotelName + "\n")
                .add(address + "\n")
                .add(contact + "\n")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16);
        return header;
    }

    public static Paragraph createCustomerDetails(String bkID, String guestName, String checkIn, String checkOut) {
        return new Paragraph()
                .add("Booking ID: " + bkID + "\n")
                .add("Guest Name: " + guestName + "\n")
                .add("Check-in:" + checkIn + "\n")
                .add("Check-out" + checkOut + "\n")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
    }

    public Table createItemizedTable(String bkid) {
        float[] columnWidths = {2, 5, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(createTableHeaderCell("Item Details"));
        table.addHeaderCell(createQtyTableHeaderCell("Qty"));
        table.addHeaderCell(createTableHeaderCell("Unit Price"));
        table.addHeaderCell(createTableHeaderCell("Total"));

        InvoiceData ivd = invoiceGeneratorController.getData(bkid);
        Duration duration = Duration.between(ivd.getCheckIn(), LocalDateTime.now());
        String durationDays = Long.toString(duration.toDays()) + "nights";

        table.addCell(ivd.getRoomType());
        table.addCell(durationDays).setTextAlignment(TextAlignment.RIGHT);
        table.addCell("50.00");
        table.addCell("100");


        double sum  = 0;
        HashMap<Integer, String> service_check = new HashMap<>();
        for(order_service os : ivd.getServices())
        {
            if(!service_check.containsKey(os.getId())){
                service_check.put(os.getId(), os.getSid().getName());
                table.addCell(os.getSid().getName());
                table.addCell(os.getQnt()+"").setTextAlignment(TextAlignment.RIGHT);
                table.addCell(os.getSid().getPrice()+"");
                table.addCell(os.getTotal_price()+"");
                sum += os.getTotal_price();
            }

        }

        HashMap<Integer, String> food_check = new HashMap<>();
        for(order_food of: ivd.getFoods()){
            if(!food_check.containsKey(of.getId())){
                food_check.put(of.getId(), of.getFid().getName());
                table.addCell(of.getFid().getName());
                table.addCell(of.getQnt()+"").setTextAlignment(TextAlignment.RIGHT);
                table.addCell(of.getFid().getPrice()+"");
                table.addCell(of.getTotal_price()+"");
                sum += of.getTotal_price();

            }

        }

        total = sum;

        return table;

    }

    public static Cell createTableHeaderCell(String text) {
        Cell cell = new Cell().setTextAlignment(TextAlignment.CENTER);
        cell.add(new Paragraph(text).setBold());
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        return cell;
    }

    public static Cell createQtyTableHeaderCell(String text) {
        Cell cell = new Cell().setTextAlignment(TextAlignment.RIGHT);
        cell.add(new Paragraph(text).setBold());
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        return cell;
    }

    public static Paragraph createTotalSection(double totalAmount) {

        Paragraph total = new Paragraph()
                .add("\nTotal: $" + String.format("%.2f", totalAmount) + "\n")
                .setTextAlignment(TextAlignment.RIGHT)
                .setBold()
                .setFontSize(14);
        return total;

    }

    public static Paragraph createFooter(String footerText) {
        return new Paragraph()
                .add("\n" + footerText)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setItalic();
    }
}
