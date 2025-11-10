package com.eshop.auth.service;

import com.eshop.auth.dto.InvoiceItemDTO;
import com.eshop.auth.dto.InvoiceRequestDTO;
import com.eshop.auth.dto.InvoiceResponseDTO;
import com.eshop.auth.entity.Order;
import com.eshop.auth.entity.OrderItem;
import com.eshop.auth.entity.NyProduct;
import com.eshop.auth.repository.OrderRepository;
import com.eshop.auth.repository.OrderItemRepository;
import com.eshop.auth.repository.NyProductRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Optional;
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
    private static final String DEFAULT_SELLER_NAME = "Nykaa E-Retail Limited";
    private static final String DEFAULT_SELLER_ADDRESS = "104, Vasan Udhyog Bhavan, Lower Parel, Mumbai – 400013";
    private static final String DEFAULT_SELLER_GSTIN = "27AAFCN5072P1ZV";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private NyProductRepository nyProductRepository;
    
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
    
    @Override
    public InvoiceService.InvoicePdfResult generateInvoicePdf(InvoiceRequestDTO request) {
        logger.info("Generating invoice PDF for order: {}", request.getOrderNo());

        try {
            String invoiceNo = generateInvoiceNumber();
            byte[] pdfBytes = buildNykaaInvoicePdf(request, invoiceNo);
            String filePath = savePdfToFile(pdfBytes, invoiceNo);
            
            return new InvoiceService.InvoicePdfResult(pdfBytes, invoiceNo, filePath);

        } catch (Exception e) {
            logger.error("Error generating invoice PDF for order: " + request.getOrderNo(), e);
            throw new RuntimeException("Failed to generate invoice PDF: " + e.getMessage(), e);
        }
    }
    
    @Override
    public InvoiceService.InvoicePdfResult generateInvoiceFromOrder(String orderNo, String token) {
        logger.info("Generating invoice from order: {} with token: {}", orderNo, token);
        
        try {
            // Fetch order
            List<Order> orders = orderRepository.findByOrderNoIn(java.util.List.of(orderNo));
            if (orders == null || orders.isEmpty()) {
                throw new RuntimeException("Order not found: " + orderNo);
            }
            Order order = orders.get(0);
            
            // Fetch order items
            List<OrderItem> orderItems = orderItemRepository.findByOrderNo(orderNo);
            if (orderItems == null || orderItems.isEmpty()) {
                throw new RuntimeException("No order items found for order: " + orderNo);
            }
            
            // Build invoice request from order data
            InvoiceRequestDTO invoiceRequest = buildInvoiceRequestFromOrder(order, orderItems, token);
            
            // Generate invoice
            return generateInvoicePdf(invoiceRequest);
            
        } catch (Exception e) {
            logger.error("Error generating invoice from order: " + orderNo, e);
            throw new RuntimeException("Failed to generate invoice from order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Build InvoiceRequestDTO from Order and OrderItems dynamically
     */
    private InvoiceRequestDTO buildInvoiceRequestFromOrder(Order order, List<OrderItem> orderItems, String token) {
        InvoiceRequestDTO request = new InvoiceRequestDTO();
        
        // Seller details (can be made configurable)
        request.setSellerName(DEFAULT_SELLER_NAME);
        request.setSellerAddress(DEFAULT_SELLER_ADDRESS);
        request.setSellerGstin(order.getGstin() != null && !order.getGstin().isEmpty() 
                ? order.getGstin() : DEFAULT_SELLER_GSTIN);
        
        // Buyer details from order
        request.setBuyerName(order.getBillToName() != null ? order.getBillToName() : order.getCustomerName());
        
        // Build buyer address
        StringBuilder buyerAddress = new StringBuilder();
        if (order.getBillAddress1() != null) buyerAddress.append(order.getBillAddress1());
        if (order.getBillAddress2() != null && !order.getBillAddress2().isEmpty()) {
            if (buyerAddress.length() > 0) buyerAddress.append(", ");
            buyerAddress.append(order.getBillAddress2());
        }
        if (order.getBillCity() != null && !order.getBillCity().isEmpty()) {
            if (buyerAddress.length() > 0) buyerAddress.append(", ");
            buyerAddress.append(order.getBillCity());
        }
        if (order.getBillState() != null && !order.getBillState().isEmpty()) {
            if (buyerAddress.length() > 0) buyerAddress.append(", ");
            buyerAddress.append(order.getBillState());
        }
        if (order.getBillZipCode() != null && !order.getBillZipCode().isEmpty()) {
            if (buyerAddress.length() > 0) buyerAddress.append(" – ");
            buyerAddress.append(order.getBillZipCode());
        }
        if (buyerAddress.length() == 0 && order.getAddress1() != null) {
            buyerAddress.append(order.getAddress1());
            if (order.getCity() != null) buyerAddress.append(", ").append(order.getCity());
            if (order.getState() != null) buyerAddress.append(", ").append(order.getState());
            if (order.getPinCode() != null) buyerAddress.append(" – ").append(order.getPinCode());
        }
        request.setBuyerAddress(buyerAddress.toString());
        
        // Order details
        request.setOrderNo(order.getOrderNo());
        request.setOrderDate(order.getOrderDate() != null 
                ? order.getOrderDate().format(DATE_FORMATTER) 
                : LocalDateTime.now().format(DATE_FORMATTER));
        request.setPaymentMode(order.getPaymentMethod() != null ? order.getPaymentMethod() : "PREPAID");
        request.setPlaceOfSupply(order.getBillState() != null ? order.getBillState() 
                : (order.getState() != null ? order.getState() : "Maharashtra(27)"));
        
        // Build invoice items from order items
        List<InvoiceItemDTO> invoiceItems = new ArrayList<>();
        int lineNo = 1;
        
        for (OrderItem item : orderItems) {
            InvoiceItemDTO invoiceItem = new InvoiceItemDTO();
            invoiceItem.setOrderNo(order.getOrderNo());
            invoiceItem.setLineNo(lineNo++);
            invoiceItem.setTransporterName(item.getTransName() != null ? item.getTransName() : "Nykaa Logistics");
            invoiceItem.setTrackingNo(item.getAwbNo() != null ? item.getAwbNo() : "");
            invoiceItem.setDescription(item.getSkuName() != null ? item.getSkuName() : "Product");
            
            // Fetch product to get HSN code
            String hsn = "";
            if (item.getSkuCode() != null && token != null) {
                Optional<NyProduct> productOpt = nyProductRepository.findBySkuAndToken(item.getSkuCode(), token);
                if (productOpt.isPresent()) {
                    hsn = productOpt.get().getHsn() != null ? productOpt.get().getHsn() : "";
                }
            }
            invoiceItem.setHsn(hsn.isEmpty() ? "996819" : hsn); // Default HSN if not found
            
            // Quantity
            invoiceItem.setQty(item.getShippedQty() != null && !item.getShippedQty().isEmpty()
                    ? Integer.parseInt(item.getShippedQty()) 
                    : (item.getOrderQty() != null && !item.getOrderQty().isEmpty()
                            ? Integer.parseInt(item.getOrderQty()) : 1));
            
            // Unit price
            invoiceItem.setUnitPrice(item.getUnitPrice() != null && !item.getUnitPrice().isEmpty()
                    ? Double.parseDouble(item.getUnitPrice()) : 0.0);
            
            // Discount
            invoiceItem.setDiscount(item.getDiscountAmount() != null && !item.getDiscountAmount().isEmpty()
                    ? Double.parseDouble(item.getDiscountAmount()) : 0.0);
            
            // Tax rate (default 5%, can be calculated from line_tax_amount if available)
            double taxableValue = (invoiceItem.getQty() * invoiceItem.getUnitPrice()) - invoiceItem.getDiscount();
            double taxRate = 5.0; // Default tax rate
            if (item.getLineTaxAmount() != null && !item.getLineTaxAmount().isEmpty() && taxableValue > 0) {
                double taxAmount = Double.parseDouble(item.getLineTaxAmount());
                taxRate = (taxAmount / taxableValue) * 100;
            }
            invoiceItem.setTaxRate(taxRate);
            
            invoiceItems.add(invoiceItem);
        }
        
        request.setOrderItemsList(invoiceItems);
        
        // AWB number (use first order item's AWB or order number)
        String awbNo = "";
        if (!orderItems.isEmpty() && orderItems.get(0).getAwbNo() != null) {
            awbNo = orderItems.get(0).getAwbNo();
        } else {
            awbNo = order.getOrderNo(); // Fallback to order number
        }
        request.setAwbNo(awbNo);
        
        return request;
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

        // Right: QR + AWB barcode (larger size for better visibility)
        Table codes = new Table(new float[]{1}).setWidth(UnitValue.createPercentValue(100));
        Image qr = new Image(ImageDataFactory.create(generateQrPng(request.getAwbNo(), 200, 200)));
        codes.addCell(new Cell().add(qr).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        Image awb = new Image(ImageDataFactory.create(generateBarcodePng(request.getAwbNo(), 280, 50)));
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

        for (int i = 0; i < items.size(); i++) {
            InvoiceItemDTO it = items.get(i);
            double taxable = (it.getQty() * it.getUnitPrice()) - it.getDiscount();
            double tax = taxable * it.getTaxRate() / 100.0;
            double cgst = 0, sgst = 0, igst = 0;
            if (intra) { cgst = tax / 2; sgst = tax / 2; } else { igst = tax; }
            double lineTotal = taxable + tax;

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
        Image qr = new Image(ImageDataFactory.create(generateQrPng(request.getAwbNo(), 200, 200)));
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
