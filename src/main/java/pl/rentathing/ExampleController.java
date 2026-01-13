package pl.rentathing;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExampleController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/finalizacja")
    public String index1() {
        return "rental/form";
    }
}
