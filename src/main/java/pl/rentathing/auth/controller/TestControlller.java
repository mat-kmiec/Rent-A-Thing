package pl.rentathing.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestControlller {


    @GetMapping("/test1")
    public String te3123st(){
        return "admin/dashboard";
    }
    @GetMapping("/test2")
    public String tes9t(){
        return "admin/inventory";
    }
    @GetMapping("/test3")
    public String tes8t(){
        return "admin/item-form";
    }
    @GetMapping("/test4")
    public String te7st(){
        return "admin/rental-form";
    }
    @GetMapping("/test5")
    public String tes6t(){
        return "admin/rentals";
    }
    @GetMapping("/test6")
    public String t5est(){
        return "admin/users";
    }

    @GetMapping("/test7")
    public String tes4t(){
        return "client/dashboard";
    }
    @GetMapping("/test8")
    public String test(){
        return "client/history";
    }
    @GetMapping("/test9")
    public String tes3t(){
        return "client/settings";
    }

}
