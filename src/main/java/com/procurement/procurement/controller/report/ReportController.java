package com.procurement.procurement.controller.report;

import com.procurement.procurement.dto.report.ReportRequestDTO;
import com.procurement.procurement.service.report.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/vendor")
    public ResponseEntity<byte[]> generateVendorReport(
            @RequestBody(required = false) ReportRequestDTO request,
            @RequestParam(defaultValue = "pdf") String format) {

        byte[] data = reportService.generateVendorReport(request, format);

        String fileName = "vendor_report." +
                (format.equalsIgnoreCase("excel") ? "xlsx" : "pdf");

        MediaType mediaType =
                format.equalsIgnoreCase("excel")
                        ? MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        : MediaType.APPLICATION_PDF;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .contentType(mediaType)
                .body(data);
    }
}
