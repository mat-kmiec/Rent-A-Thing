package pl.rentathing.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.rentathing.Rental.Dto.ClientDashboardDTO;
import pl.rentathing.user.entity.User;
import pl.rentathing.user.service.UserDashboardService;
import pl.rentathing.user.service.UserService;

import java.util.Optional;

/**
 * Controller responsible for managing and handling the user dashboard functionality.
 * Provides endpoints for rendering user-specific views and data for the client dashboard.
 */
@Controller
@RequiredArgsConstructor
public class UserDashboardController {

    private final UserDashboardService dashboardService;
    private final UserService userService;

    /**
     * Displays the client dashboard view populated with user-specific dashboard data.
     *
     * @param model the model object used to pass attributes to the view
     * @param userDetails the details of the currently authenticated user
     * @return the name of the view template to render the dashboard
     */
    @GetMapping("/moje-konto")
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user = userService.findByEmail(userDetails.getUsername());

        ClientDashboardDTO dashboardData = dashboardService.getClientDashboardData(user.get().getId());
        model.addAttribute("dashboard", dashboardData);

        return "client/dashboard";
    }
}
