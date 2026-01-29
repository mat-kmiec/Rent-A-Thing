package pl.rentathing.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import pl.rentathing.user.dto.UserListDTO;
import pl.rentathing.user.service.UserService;
import pl.rentathing.user.service.CsvService;
import pl.rentathing.user.dto.UserSettingsDTO;
import pl.rentathing.user.dto.AddressDTO;
import pl.rentathing.user.entity.User;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final CsvService csvService;

    @GetMapping()
    public String getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<UserListDTO> users = userService.searchUsers(search, role, status, pageable);

        int totalPages = users.getTotalPages();
        List<Integer> pages = new ArrayList<>();
        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(totalPages - 1, page + 2);
        for (int i = startPage; i <= endPage; i++) {
            pages.add(i);
        }

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pages", pages);
        model.addAttribute("search", search);
        model.addAttribute("role", role);
        model.addAttribute("status", status);

        return "admin/users";
    }

    @PostMapping("/{id}/block")
    @ResponseBody
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @PostMapping("/{id}/unlock")
    @ResponseBody
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @GetMapping("/export-csv")
    public ResponseEntity<String> exportUsersCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {

        try {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Page<UserListDTO> users = userService.searchUsers(search, role, status, pageable);

            String csvContent = csvService.generateUsersCsv(users.getContent());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users.csv\"")
                    .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                    .body(csvContent);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd przy generowaniu pliku CSV");
        }
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        AddressDTO addressDTO = null;
        if (user.getAddress() != null) {
            addressDTO = AddressDTO.builder()
                    .street(user.getAddress().getStreet())
                    .houseNumber(user.getAddress().getHouseNumber())
                    .apartmentNumber(user.getAddress().getApartmentNumber())
                    .city(user.getAddress().getCity())
                    .zipCode(user.getAddress().getZipCode())
                    .build();
        } else {
            addressDTO = AddressDTO.builder().build();
        }

        UserSettingsDTO settingsDTO = UserSettingsDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(addressDTO)
                .build();

        model.addAttribute("userSettings", settingsDTO);
        return "admin/user-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
            @Valid @ModelAttribute("userSettings") UserSettingsDTO settingsDTO,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/user-edit";
        }

        try {
            userService.updateUserSettings(id, settingsDTO);
        } catch (Exception e) {
            model.addAttribute("error", "Błąd podczas aktualizacji: " + e.getMessage());
            return "admin/user-edit";
        }

        return "redirect:/admin/users?success=Zaktualizowano dane użytkownika";
    }
}
