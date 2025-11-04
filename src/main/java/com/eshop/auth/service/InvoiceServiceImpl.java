package com.eshop.auth.service;

import com.eshop.auth.dto.InvoiceItemDTO;
import com.eshop.auth.dto.InvoiceRequestDTO;
import com.eshop.auth.dto.InvoiceResponseDTO;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

/**
 * Service implementation for invoice generation
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private static final String INVOICE_DIR = "./invoices";
    private static final String INVOICE_PREFIX = "INV-NYKA-";
    
    public InvoiceServiceImpl() {
        // Create invoices directory if it doesn't exist
        createInvoiceDirectory();
    }
    
    @Override
    public InvoiceResponseDTO generateInvoice(InvoiceRequestDTO request) {
        logger.info("Generating invoice for order: {}", request.getOrderNo());

        try {
            String invoiceNo = generateInvoiceNumber();
            byte[] pdfBytes = buildNykaaInvoicePdf(request, invoiceNo);

            String filePath = savePdfToFile(pdfBytes, invoiceNo);
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            InvoiceResponseDTO response = new InvoiceResponseDTO();
            response.setResponseCode(0);
            response.setResponseMessage("Success");
            response.setInvoiceNo(invoiceNo);
            response.setPdfBase64(pdfBase64);
            response.setFilePath(filePath);
            return response;

        } catch (Exception e) {
            logger.error("Error generating invoice for order: " + request.getOrderNo(), e);
            throw new RuntimeException("Failed to generate invoice: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate unique invoice number
     * Format: INV-NYKA-YYYYMMDDHHMMSS
     */
    private String generateInvoiceNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        return INVOICE_PREFIX + timestamp;
    }
    
    /**
     * Save PDF to file system
     */
    private String savePdfToFile(byte[] pdfBytes, String invoiceNo) throws IOException {
        String fileName = invoiceNo + ".pdf";
        String filePath = INVOICE_DIR + "/" + fileName;
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
            fos.flush();
        }
        
        return filePath;
    }
    
    /**
     * Create invoices directory if it doesn't exist
     */
    private void createInvoiceDirectory() {
        File directory = new File(INVOICE_DIR);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Created invoices directory: {}", INVOICE_DIR);
            } else {
                logger.warn("Failed to create invoices directory: {}", INVOICE_DIR);
            }
        }
    }

    // ---------------- PDF GENERATION (iText7) ----------------
    private byte[] buildNykaaInvoicePdf(InvoiceRequestDTO request, String invoiceNo) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        // Page 1: Tax Invoice (Original for Recipient)
        addTopHeader(document, request, invoiceNo);
        addBuyerSellerBlock(document, request);
        addItemsTable(document, request.getOrderItemsList(), request.getPlaceOfSupply());
        addTotalsBlock(document, request.getOrderItemsList(), request.getPlaceOfSupply());
        addNykaaFooter(document);

        // Page 2: Shipping charges/summary (as per sample second page)
        pdf.addNewPage();
        Document page2 = new Document(pdf, PageSize.A4);
        page2.setMargins(20, 20, 20, 20);
        addPage2Header(page2, request, invoiceNo);
        addShippingChargesTable(page2);
        addNykaaFooter(page2);
        page2.close();

        document.close();
        return baos.toByteArray();
    }

    // ---------------- Sections ----------------
    private void addTopHeader(Document document, InvoiceRequestDTO request, String invoiceNo) throws Exception {
        float[] three = {200f, 160f, 160f};
        Table row = new Table(three).setWidth(UnitValue.createPercentValue(100));

        // Left: NYKAA MAN (simple text)
        Paragraph brand = new Paragraph("NYKAA\nMAN").setBold().setFontSize(28).setTextAlignment(TextAlignment.LEFT);
        row.addCell(new Cell().add(brand).setBorder(Border.NO_BORDER));

        // Center: Invoice title
        Paragraph title = new Paragraph("RETAIL / TAX INVOICE\n(Original For Recipient)")
                .setBold().setTextAlignment(TextAlignment.CENTER);
        row.addCell(new Cell().add(title).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));

        // Right: QR + AWB barcode
        Table codes = new Table(new float[]{1}).setWidth(UnitValue.createPercentValue(100));
        Image qr = new Image(ImageDataFactory.create(generateQrPng(request.getAwbNo(), 140, 140)));
        codes.addCell(new Cell().add(qr).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        Image awb = new Image(ImageDataFactory.create(generateBarcodePng(request.getAwbNo(), 240, 32)));
        codes.addCell(new Cell().add(awb).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        row.addCell(new Cell().add(codes).setBorder(Border.NO_BORDER));

        document.add(row);

        // Invoice meta table (like sample top band)
        Table meta = new Table(new float[]{220f, 220f}).setWidth(UnitValue.createPercentValue(100));
        meta.addCell(cellNoBorder("Invoice Number: " + invoiceNo, true));
        meta.addCell(cellRightNoBorder("AWB NO: " + request.getAwbNo(), false));
        meta.addCell(cellNoBorder("Invoice Date: " + request.getOrderDate(), false));
        meta.addCell(cellRightNoBorder("Order Number: " + request.getOrderNo(), false));
        document.add(meta);
        document.add(spacer(6));
    }

    private void addBuyerSellerBlock(Document document, InvoiceRequestDTO req) {
        float[] two = {50f, 50f};
        Table box = new Table(two).setWidth(UnitValue.createPercentValue(100));

        String seller = req.getSellerName() + "\n" + req.getSellerAddress() + "\nGSTIN: " + req.getSellerGstin();
        box.addCell(new Cell().add(new Paragraph("Seller Details:").setBold()).add(new Paragraph(seller))
                .setBorder(new SolidBorder(1)));

        String billShip = "SHIPPING & BILLING ADDRESS :\n" + req.getBuyerName() + "\n" + req.getBuyerAddress() +
                "\nBuyer UID/ GSTIN #: Unregistered";
        box.addCell(new Cell().add(new Paragraph(billShip)).setBorder(new SolidBorder(1)));
        document.add(box);
        document.add(spacer(6));
    }

    private void addItemsTable(Document document, List<InvoiceItemDTO> items, String placeOfSupply) {
        DecimalFormat df = new DecimalFormat("#0.00");
        boolean intra = isIntraState(placeOfSupply);

        float[] widths = {30f, 210f, 60f, 40f, 70f, 70f, 80f, 60f, 60f, 60f, 70f};
        Table table = new Table(widths).setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"S.No", "Description", "HSN", "Qty", "Unit Price", "Discount", "Taxable Value",
                "CGST", "SGST/UTGST", "IGST", "Total"};
        for (String h : headers) table.addCell(headerCell(h));

        double totalAmount = 0, totalTax = 0;
        for (int i = 0; i < items.size(); i++) {
            InvoiceItemDTO it = items.get(i);
            double taxable = (it.getQty() * it.getUnitPrice()) - it.getDiscount();
            double tax = taxable * it.getTaxRate() / 100.0;
            double cgst = 0, sgst = 0, igst = 0;
            if (intra) { cgst = tax / 2; sgst = tax / 2; } else { igst = tax; }
            double lineTotal = taxable + tax;
            totalAmount += taxable; totalTax += tax;

            table.addCell(bodyCell(String.valueOf(i + 1), TextAlignment.CENTER));
            table.addCell(bodyCell(it.getDescription(), TextAlignment.LEFT));
            table.addCell(bodyCell(it.getHsn(), TextAlignment.CENTER));
            table.addCell(bodyCell(String.valueOf(it.getQty()), TextAlignment.CENTER));
            table.addCell(bodyCell(df.format(it.getUnitPrice()), TextAlignment.RIGHT));
            table.addCell(bodyCell(df.format(it.getDiscount()), TextAlignment.RIGHT));
            table.addCell(bodyCell(df.format(taxable), TextAlignment.RIGHT));
            table.addCell(bodyCell(df.format(cgst), TextAlignment.RIGHT));
            table.addCell(bodyCell(df.format(sgst), TextAlignment.RIGHT));
            table.addCell(bodyCell(df.format(igst), TextAlignment.RIGHT));
            table.addCell(bodyCell(df.format(lineTotal), TextAlignment.RIGHT));
        }

        document.add(table);
    }

    private void addTotalsBlock(Document document, List<InvoiceItemDTO> items, String placeOfSupply) {
        DecimalFormat df = new DecimalFormat("#0.00");
        boolean intra = isIntraState(placeOfSupply);
        double totalAmount = 0, totalTax = 0;
        for (InvoiceItemDTO it : items) {
            double taxable = (it.getQty() * it.getUnitPrice()) - it.getDiscount();
            double tax = taxable * it.getTaxRate() / 100.0;
            totalAmount += taxable; totalTax += tax;
        }

        double net = totalAmount + totalTax;

        Table totals = new Table(new float[]{200f, 120f}).setWidth(UnitValue.createPercentValue(40))
                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
        totals.addCell(cellNoBorder("Total Amount (+)", true));
        totals.addCell(cellRightNoBorder(df.format(totalAmount), false));
        totals.addCell(cellNoBorder("Total Tax (" + (intra ? "CGST+SGST" : "IGST") + ")", true));
        totals.addCell(cellRightNoBorder(df.format(totalTax), false));
        totals.addCell(cellNoBorder("Net Payable", true));
        totals.addCell(cellRightNoBorder(df.format(net), true));
        document.add(totals);
    }

    private void addPage2Header(Document doc, InvoiceRequestDTO request, String invoiceNo) throws Exception {
        float[] three = {200f, 160f, 160f};
        Table row = new Table(three).setWidth(UnitValue.createPercentValue(100));
        row.addCell(new Cell().add(new Paragraph("NYKAA\nMAN").setBold().setFontSize(28)).setBorder(Border.NO_BORDER));
        row.addCell(new Cell().add(new Paragraph("RETAIL / TAX INVOICE\n(Original For Recipient)").setBold().setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
        Table codes = new Table(new float[]{1}).setWidth(UnitValue.createPercentValue(100));
        Image qr = new Image(ImageDataFactory.create(generateQrPng(request.getAwbNo(), 120, 120)));
        codes.addCell(new Cell().add(qr).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        row.addCell(new Cell().add(codes).setBorder(Border.NO_BORDER));
        doc.add(row);

        Table meta = new Table(new float[]{220f, 220f}).setWidth(UnitValue.createPercentValue(100));
        meta.addCell(cellNoBorder("Invoice Number: " + invoiceNo, true));
        meta.addCell(cellRightNoBorder("Order Number: " + request.getOrderNo(), false));
        doc.add(meta);
        doc.add(spacer(6));
    }

    private void addShippingChargesTable(Document document) {
        Table t = new Table(new float[]{40f, 260f, 60f, 50f, 70f, 70f, 70f})
                .setWidth(UnitValue.createPercentValue(100));

        String[] h = {"S.No", "Description", "HSN", "Qty", "Taxable Value(INR)", "IGST Rate%", "IGST Amt(INR)"};
        for (String x : h) t.addCell(headerCell(x));

        t.addCell(bodyCell("1", TextAlignment.CENTER));
        t.addCell(bodyCell("Shipping Charges", TextAlignment.LEFT));
        t.addCell(bodyCell("996819", TextAlignment.CENTER));
        t.addCell(bodyCell("1", TextAlignment.CENTER));
        t.addCell(bodyCell("16.10", TextAlignment.RIGHT));
        t.addCell(bodyCell("18.00", TextAlignment.RIGHT));
        t.addCell(bodyCell("2.90", TextAlignment.RIGHT));

        document.add(t);

        Table totals = new Table(new float[]{200f, 120f}).setWidth(UnitValue.createPercentValue(40))
                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
        totals.addCell(cellNoBorder("Total Amount (+)", true));
        totals.addCell(cellRightNoBorder("16.10", false));
        totals.addCell(cellNoBorder("Total Tax (+)", true));
        totals.addCell(cellRightNoBorder("2.90", false));
        totals.addCell(cellNoBorder("Net Payable", true));
        totals.addCell(cellRightNoBorder("19.00", true));
        document.add(totals);
    }

    private void addNykaaFooter(Document document) {
        document.add(spacer(10));
        document.add(new Paragraph("DECLARATION 1: Tax is not payable on reverse charge basis.").setFontSize(9));
        document.add(new Paragraph("CUSTOMER SELF DECLARATION: I, Hereby confirm that the content of this package are being purchased for my internal and personal purpose and not for resale.").setFontSize(9));
        document.add(spacer(6));
        document.add(new Paragraph("Authorised Signatory").setTextAlignment(TextAlignment.RIGHT));
    }

    // ---------------- Helpers ----------------
    private Cell headerCell(String text) {
        return new Cell().add(new Paragraph(text).setBold()).setTextAlignment(TextAlignment.CENTER);
    }

    private Cell bodyCell(String text, TextAlignment align) {
        return new Cell().add(new Paragraph(text == null ? "" : text)).setTextAlignment(align);
    }

    private Cell cellNoBorder(String text, boolean bold) {
        Paragraph p = new Paragraph(text);
        if (bold) p.setBold();
        return new Cell().add(p).setBorder(Border.NO_BORDER);
    }

    private Cell cellRightNoBorder(String text, boolean bold) {
        Paragraph p = new Paragraph(text).setTextAlignment(TextAlignment.RIGHT);
        if (bold) p.setBold();
        return new Cell().add(p).setBorder(Border.NO_BORDER);
    }

    private Paragraph spacer(float pts) {
        return new Paragraph(" ").setMarginTop(pts).setMarginBottom(pts).setBorder(Border.NO_BORDER);
    }

    private boolean isIntraState(String placeOfSupply) {
        if (placeOfSupply == null) return true;
        return placeOfSupply.contains("(27)") || placeOfSupply.toLowerCase().contains("maharashtra");
    }

    private byte[] generateBarcodePng(String text, int width, int height) throws WriterException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        var matrix = new MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, width, height);
        MatrixToImageWriter.writeToStream(matrix, "png", baos);
        return baos.toByteArray();
    }

    private byte[] generateQrPng(String text, int width, int height) throws WriterException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        var matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToStream(matrix, "png", baos);
        return baos.toByteArray();
    }
}
