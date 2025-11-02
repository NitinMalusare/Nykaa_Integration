package com.eshop.auth.util;

import com.eshop.auth.dto.InvoiceItemDTO;
import com.eshop.auth.dto.InvoiceRequestDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Utility class for generating Nykaa-style invoice PDFs using OpenPDF
 * Fully corrected and formatted version
 */
public class InvoicePdfBuilder {

    private static final Logger logger = LoggerFactory.getLogger(InvoicePdfBuilder.class);
    private static final DecimalFormat df = new DecimalFormat("#0.00");

    /**
     * Generate PDF invoice as byte array
     */
    public static byte[] generatePdf(InvoiceRequestDTO request, String invoiceNo) {
        logger.info("Generating PDF invoice: {}", invoiceNo);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36); // Added margins

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            addHeader(document, request, invoiceNo);
            addBuyerSellerInfo(document, request);
            addItemsTable(document, request);
            addTotalsSection(document, request);
            addFooter(document);

            logger.info("PDF generated successfully for invoice: {}", invoiceNo);
        } catch (Exception e) {
            logger.error("Error generating PDF invoice", e);
            throw new RuntimeException("Failed to generate invoice PDF: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) document.close();
        }

        return baos.toByteArray();
    }

    /** ---------------- HEADER ---------------- **/
    private static void addHeader(Document document, InvoiceRequestDTO request, String invoiceNo)
            throws DocumentException {

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        Paragraph title = new Paragraph("TAX INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{55, 45});

        // Left Column (Seller)
        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Paragraph(request.getSellerName(), normalFont));
        left.addElement(new Paragraph(request.getSellerAddress(), normalFont));
        left.addElement(new Paragraph("GSTIN: " + request.getSellerGstin(), normalFont));
        header.addCell(left);

        // Right Column (Invoice Details)
        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);

        String invoiceDetails =
                "Invoice No: " + invoiceNo + "\n" +
                "Invoice Date: " + request.getOrderDate() + "\n" +
                "AWB No: " + request.getAwbNo() + "\n" +
                "Order No: " + request.getOrderNo() + "\n" +
                "Payment Mode: " + request.getPaymentMode() + "\n" +
                "Place of Supply: " + request.getPlaceOfSupply();

        right.addElement(new Paragraph(invoiceDetails, normalFont));
        header.addCell(right);

        document.add(header);
        document.add(Chunk.NEWLINE);
    }

    /** ---------------- BUYER / SHIPPING ---------------- **/
    private static void addBuyerSellerInfo(Document document, InvoiceRequestDTO request)
            throws DocumentException {

        Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{50, 50});

        PdfPCell buyer = new PdfPCell();
        buyer.setBorder(Rectangle.BOX);
        buyer.setPadding(6);
        buyer.addElement(new Paragraph("BILL TO:", boldFont));
        buyer.addElement(new Paragraph(request.getBuyerName(), normalFont));
        buyer.addElement(new Paragraph(request.getBuyerAddress(), normalFont));
        info.addCell(buyer);

        PdfPCell ship = new PdfPCell();
        ship.setBorder(Rectangle.BOX);
        ship.setPadding(6);
        ship.addElement(new Paragraph("SHIP TO:", boldFont));
        ship.addElement(new Paragraph(request.getBuyerName(), normalFont));
        ship.addElement(new Paragraph(request.getBuyerAddress(), normalFont));
        info.addCell(ship);

        document.add(info);
        document.add(Chunk.NEWLINE);
    }

    /** ---------------- ITEM TABLE ---------------- **/
    private static void addItemsTable(Document document, InvoiceRequestDTO request)
            throws DocumentException {

        Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 22, 8, 5, 8, 8, 10, 8, 8, 8, 10});

        // Table Header
        String[] headers = {"S.No", "Description", "HSN", "Qty", "Unit Price", "Discount",
                "Taxable Value", "CGST Amt", "SGST Amt", "IGST Amt", "Total"};
        for (String h : headers) addTableHeader(table, h, headerFont);

        List<InvoiceItemDTO> items = request.getOrderItemsList();
        boolean intraState = isIntraState(request.getPlaceOfSupply());

        for (int i = 0; i < items.size(); i++) {
            InvoiceItemDTO item = items.get(i);
            double taxableValue = (item.getQty() * item.getUnitPrice()) - item.getDiscount();
            double taxAmount = taxableValue * item.getTaxRate() / 100;

            double cgst = 0.0, sgst = 0.0, igst = 0.0;
            if (intraState) {
                cgst = taxAmount / 2;
                sgst = taxAmount / 2;
            } else {
                igst = taxAmount;
            }
            double total = taxableValue + taxAmount;

            addTableCell(table, String.valueOf(i + 1), normalFont, Element.ALIGN_CENTER);
            addTableCell(table, item.getDescription(), normalFont, Element.ALIGN_LEFT);
            addTableCell(table, item.getHsn(), normalFont, Element.ALIGN_CENTER);
            addTableCell(table, String.valueOf(item.getQty()), normalFont, Element.ALIGN_CENTER);
            addTableCell(table, df.format(item.getUnitPrice()), normalFont, Element.ALIGN_RIGHT);
            addTableCell(table, df.format(item.getDiscount()), normalFont, Element.ALIGN_RIGHT);
            addTableCell(table, df.format(taxableValue), normalFont, Element.ALIGN_RIGHT);
            addTableCell(table, df.format(cgst), normalFont, Element.ALIGN_RIGHT);
            addTableCell(table, df.format(sgst), normalFont, Element.ALIGN_RIGHT);
            addTableCell(table, df.format(igst), normalFont, Element.ALIGN_RIGHT);
            addTableCell(table, df.format(total), normalFont, Element.ALIGN_RIGHT);
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    /** ---------------- TOTALS SECTION ---------------- **/
    private static void addTotalsSection(Document document, InvoiceRequestDTO request)
            throws DocumentException {

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        PdfPTable totals = new PdfPTable(2);
        totals.setWidthPercentage(50);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totals.setWidths(new float[]{65, 35});

        double totalAmount = 0.0;
        double totalTax = 0.0;
        boolean intraState = isIntraState(request.getPlaceOfSupply());

        for (InvoiceItemDTO item : request.getOrderItemsList()) {
            double taxable = (item.getQty() * item.getUnitPrice()) - item.getDiscount();
            double tax = taxable * item.getTaxRate() / 100;
            totalAmount += taxable;
            totalTax += tax;
        }

        double netPayable = totalAmount + totalTax;

        addTotalRow(totals, "Total Amount (+)", df.format(totalAmount), normalFont, headerFont);
        addTotalRow(totals, "Total Tax (" + (intraState ? "CGST+SGST" : "IGST") + ")", df.format(totalTax), normalFont, headerFont);
        addTotalRow(totals, "Net Payable", df.format(netPayable), headerFont, headerFont);

        document.add(totals);
        document.add(Chunk.NEWLINE);
    }

    /** ---------------- FOOTER ---------------- **/
    private static void addFooter(Document document) throws DocumentException {
        Font normalFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

        Paragraph declaration = new Paragraph(
                "Declaration: This is a system-generated invoice. No physical signature required.",
                normalFont
        );
        declaration.setAlignment(Element.ALIGN_CENTER);
        declaration.setSpacingBefore(20);
        document.add(declaration);

        Paragraph sign = new Paragraph("Authorised Signatory", normalFont);
        sign.setAlignment(Element.ALIGN_RIGHT);
        sign.setSpacingBefore(25);
        document.add(sign);
    }

    /** ---------------- UTIL METHODS ---------------- **/
    private static void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static void addTotalRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell left = new PdfPCell(new Phrase(label, labelFont));
        left.setBorder(Rectangle.NO_BORDER);
        PdfPCell right = new PdfPCell(new Phrase(value, valueFont));
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(left);
        table.addCell(right);
    }

    /** Determines if intra-state transaction (Maharashtra(27) default) **/
    private static boolean isIntraState(String placeOfSupply) {
        if (placeOfSupply == null) return true;
        return placeOfSupply.contains("(27)") || placeOfSupply.toLowerCase().contains("maharashtra");
    }
}
