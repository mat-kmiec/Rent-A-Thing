package pl.rentathing.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rentathing.Rental.Entity.Rental;
import pl.rentathing.Rental.Repository.RentalRepository;
import pl.rentathing.item.entity.Item;
import pl.rentathing.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final RentalRepository rentalRepository;
    private final pl.rentathing.item.repository.ItemRepository itemRepository;
    private final pl.rentathing.user.repository.UserRepository userRepository;

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        BigDecimal revenue = rentalRepository.calculateRevenueBetween(startOfMonth, now);
        model.addAttribute("currentMonthRevenue", revenue != null ? revenue : BigDecimal.ZERO);

        // 1. Resource Utilization
        long activeRentalsCount = rentalRepository.countByStatus(pl.rentathing.Rental.Entity.RentalStatus.ACTIVE);
        long totalItemsCount = itemRepository.count();
        int utilizationPercentage = totalItemsCount > 0 ? (int) ((activeRentalsCount * 100) / totalItemsCount) : 0;

        model.addAttribute("activeRentalsCount", activeRentalsCount);
        model.addAttribute("utilizationPercentage", utilizationPercentage);

        // 2. Returns Today & Overdue
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        long returnsTodayCount = rentalRepository.countByEndDateTimeBetween(startOfDay, endOfDay);
        long overdueCount = rentalRepository.countByStatus(pl.rentathing.Rental.Entity.RentalStatus.OVERDUE);

        model.addAttribute("returnsTodayCount", returnsTodayCount);
        model.addAttribute("overdueCount", overdueCount);

        // 3. Registered Users
        long totalUsersCount = userRepository.count();
        model.addAttribute("totalUsersCount", totalUsersCount);
        model.addAttribute("newUsersCount", 0); // Placeholder until createdAt is added to User entity

        List<Object[]> topItems = rentalRepository.findTopRentedItems(PageRequest.of(0, 5));
        List<String> topItemLabels = new ArrayList<>();
        List<Long> topItemData = new ArrayList<>();
        for (Object[] row : topItems) {
            Item item = (Item) row[0];
            Long count = (Long) row[1];
            topItemLabels.add(item.getTitle());
            topItemData.add(count);
        }
        model.addAttribute("topItemLabels", topItemLabels);
        model.addAttribute("topItemData", topItemData);

        List<Object[]> topUsers = rentalRepository.findTopUsers(PageRequest.of(0, 5));
        List<String> topUserLabels = new ArrayList<>();
        List<Long> topUserData = new ArrayList<>();
        for (Object[] row : topUsers) {
            User user = (User) row[0];
            Long count = (Long) row[1];
            topUserLabels.add(user.getFirstName() + " " + user.getLastName());
            topUserData.add(count);
        }
        model.addAttribute("topUserLabels", topUserLabels);
        model.addAttribute("topUserData", topUserData);

        List<Rental> latestRentals = rentalRepository.findTop5ByOrderByStartDateTimeDesc();
        model.addAttribute("latestRentals", latestRentals);

        return "admin/dashboard";
    }
}
