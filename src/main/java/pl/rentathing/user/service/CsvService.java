package pl.rentathing.user.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.rentathing.user.dto.UserListDTO;
import java.io.StringWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvService {

    public String generateUsersCsv(List<UserListDTO> users) throws Exception {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);

        // Nagłówki
        String[] header = {"ID", "Imię", "Nazwisko", "Email", "Rola", "Aktywny", "Zablokowany"};
        writer.writeNext(header);

        // Dane użytkowników
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
