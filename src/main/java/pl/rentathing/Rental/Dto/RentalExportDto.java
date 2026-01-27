package pl.rentathing.Rental.Dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
