package pl.rentathing.user.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.user.dto.UserListDTO;
import java.io.StringWriter;
import java.util.List;

/**
 * Service for generating CSV data from user information.
 */
@Service
@RequiredArgsConstructor
public class CsvService {

    /**
     * Generates a CSV-formatted string containing user data from the provided list of UserListDTO objects.
     *
     * @param users the list of UserListDTO objects containing user data to be written to the CSV
     * @return a String representation of the users' data in CSV format
     * @throws Exception if an I/O error occurs during CSV generation
     */
    public String generateUsersCsv(List<UserListDTO> users) throws Exception {
        StringWriter sw = new StringWriter();
        sw.write('\uFEFF');
        CSVWriter writer = new CSVWriter(sw);

        String[] header = { "ID", "Imię", "Nazwisko", "Email", "Rola", "Aktywny", "Zablokowany" };
        writer.writeNext(header);

        for (UserListDTO user : users) {
            String[] line = {
                    user.getId().toString(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getEnabled() ? "Tak" : "Nie",
                    user.getLocked() ? "Tak" : "Nie"
            };
            writer.writeNext(line);
        }

        writer.close();
        return sw.toString();
    }
}
