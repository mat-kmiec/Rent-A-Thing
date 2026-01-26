package pl.rentathing.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rentathing.admin.service.PdfReportService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final PdfReportService pdfReportService;

    @GetMapping("/dashboard-pdf")
    public ResponseEntity<byte[]> generateDashboardReport() {
        try {
            byte[] pdfContent = pdfReportService.generateDashboardReport();

            String filename = "Raport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
                    + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);

        } catch (IOException e) {
            e.printStackTrace(); // In prod, log this
            return ResponseEntity.internalServerError().build();
        }
    }
}
