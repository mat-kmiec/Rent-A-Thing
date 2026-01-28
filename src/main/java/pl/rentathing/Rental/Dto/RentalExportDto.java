package pl.rentathing.Rental.Dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used for exporting rental information.
 *
 * This class is designed for scenarios where rental data needs to be exported,
 * such as generating reports or exporting to a CSV file. It encapsulates fundamental
 * rental details including the rental ID, item title, customer details, rental duration,
 * costs, and status.
 *
 * Key Fields:
 * - id: The unique identifier of the rental record.
 * - itemTitle: The title of the rented item.
 * - userFullName: The full name of the customer who rented the item.
 * - startDateTime: The start date and time of the rental period.
 * - endDateTime: The end date and time of the rental period.
 * - totalCost: The total cost associated with the rental.
 * - status: The current status of the rental.
 */
@Getter @Setter
public class RentalExportDto {
    @CsvBindByName(column = "ID")
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "Przedmiot")
    @CsvBindByPosition(position = 1)
    private String itemTitle;

    @CsvBindByName(column = "Klient")
    @CsvBindByPosition(position = 2)
    private String userFullName;

    @CsvBindByName(column = "Data Rozpoczecia")
    @CsvBindByPosition(position = 3)
    private String startDateTime;

    @CsvBindByName(column = "Data Zakonczenia")
    @CsvBindByPosition(position = 4)
    private String endDateTime;

    @CsvBindByName(column = "Koszt Calkowity")
    @CsvBindByPosition(position = 5)
    private String totalCost;

    @CsvBindByName(column = "Status")
    @CsvBindByPosition(position = 6)
    private String status;
}
