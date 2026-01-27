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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RentalExportService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

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
