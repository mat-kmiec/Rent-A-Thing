package pl.rentathing.Rental.Service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.Rental.Dto.RentalExportDto;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Mapper.RentalMapper;
import pl.rentathing.Rental.Repository.RentalRepository;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;


/**
 * Service responsible for exporting rental data to a CSV format.
 * It retrieves rental data within a specified date range from the repository
 * and maps it into a format suitable for export using OpenCSV.
 *
 * Dependencies:
 * - RentalRepository: Used to fetch rental data from the database.
 * - RentalMapper: Used to transform Rental entities into DTOs for export.
 */
@Service
@RequiredArgsConstructor
public class RentalExportService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

    /**
     * Exports rental data within the specified date range to a CSV format.
     *
     * This method retrieves rental records matching the provided start and end dates, converts them
     * into a structured format suitable for CSV export, and generates a CSV file as a byte array.
     * The generated CSV uses ';' as the separator and does not apply quotes to all fields.
     *
     * @param from the start date of the rental period, inclusive
     * @param to the end date of the rental period, inclusive
     * @return a byte array representing the CSV file containing rental data
     */
    public byte[] exportRentalsToCsv(LocalDate from, LocalDate to) {
        List<Rental> rentals = rentalRepository.findAllByStartDateTimeBetweenWithDetails(
                from.atStartOfDay(),
                to.atTime(23, 59, 59)
        );

        List<RentalExportDto> exportData = rentalMapper.toExportDtoList(rentals);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {

            StatefulBeanToCsv<RentalExportDto> beanToCsv = new StatefulBeanToCsvBuilder<RentalExportDto>(writer)
                    .withSeparator(';')
                    .withApplyQuotesToAll(false)
                    .build();

            beanToCsv.write(exportData);
            writer.flush();
            return stream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania CSV", e);
        }
    }
}
