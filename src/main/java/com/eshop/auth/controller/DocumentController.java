package com.eshop.auth.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/RestWS/api/sellerPanel/v3/documents")
public class DocumentController {

    @GetMapping("/fetch")
    public ResponseEntity<ByteArrayResource> fetchDocument(
            @RequestParam("invoiceNo") String invoiceNo,
            @RequestParam("documentType") String documentType) throws IOException {

        // For sandbox, look up generated PDFs under ./invoices
        Path p = Path.of("./invoices/" + invoiceNo + ".pdf");
        if (!Files.exists(p)) {
            return ResponseEntity.notFound().build();
        }
        byte[] content = Files.readAllBytes(p);
        ByteArrayResource resource = new ByteArrayResource(content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", invoiceNo + "-" + documentType + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}


