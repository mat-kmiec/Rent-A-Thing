package pl.rentathing.admin.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Entity.RentalStatus;
import pl.rentathing.Rental.Repository.RentalRepository;
import pl.rentathing.user.repository.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public byte[] generateDashboardReport() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED, 18,
                    Font.BOLD);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED, 12,
                    Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED, 10,
                    Font.NORMAL);

            Paragraph title = new Paragraph("Raport Operacyjny Rent-A-Thing", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            document.add(new Paragraph("Data wygenerowania: " + dateStr, normalFont));

            document.add(new Paragraph(" "));

            addStatsSection(document, headerFont, normalFont);

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Aktywne Wypożyczenia", headerFont));
            document.add(new Paragraph(" ", normalFont));
            addRentalsTable(document, RentalStatus.ACTIVE, headerFont, normalFont);

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Opóźnione Zwroty", headerFont));
            document.add(new Paragraph(" ", normalFont));
            addRentalsTable(document, RentalStatus.OVERDUE, headerFont, normalFont);

            document.close();
            return out.toByteArray();
        }
    }

    private void addStatsSection(Document document, Font headerFont, Font normalFont) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        BigDecimal revenue = rentalRepository.calculateRevenueBetween(startOfMonth, now);
        long activeRentals = rentalRepository.countByStatus(RentalStatus.ACTIVE);
        long overdueRentals = rentalRepository.countByStatus(RentalStatus.OVERDUE);
        long totalUsers = userRepository.count();

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell(new PdfPCell(new Phrase("Przychód (Bieżący mies.)", headerFont)));
        table.addCell(new PdfPCell(new Phrase((revenue != null ? revenue.toString() : "0.00") + " PLN", normalFont)));

        table.addCell(new PdfPCell(new Phrase("Aktywne wypożyczenia", headerFont)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(activeRentals), normalFont)));

        table.addCell(new PdfPCell(new Phrase("Opóźnione zwroty", headerFont)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(overdueRentals), normalFont)));

        table.addCell(new PdfPCell(new Phrase("Liczba użytkowników", headerFont)));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(totalUsers), normalFont)));

        document.add(table);
    }

    private void addRentalsTable(Document document, RentalStatus status, Font headerFont, Font normalFont) {
        List<Rental> rentals = rentalRepository.findByStatus(status);

        if (rentals.isEmpty()) {
            document.add(new Paragraph("Brak danych.", normalFont));
            return;
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 1, 3, 3, 2 });

        table.addCell(new PdfPCell(new Phrase("ID", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Przedmiot", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Klient", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Do zwrotu", headerFont)));

        for (Rental rental : rentals) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(rental.getId()), normalFont)));
            table.addCell(new PdfPCell(new Phrase(rental.getItem().getTitle(), normalFont)));
            table.addCell(new PdfPCell(
                    new Phrase(rental.getUser().getFirstName() + " " + rental.getUser().getLastName(), normalFont))); // Assumption:
                                                                                                                      // User
                                                                                                                      // has
                                                                                                                      // firstName/lastName
            table.addCell(new PdfPCell(
                    new Phrase(rental.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), normalFont)));
        }

        document.add(table);
    }
}
