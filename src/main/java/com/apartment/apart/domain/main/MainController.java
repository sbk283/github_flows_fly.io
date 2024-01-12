package com.apartment.apart.domain.main;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.user.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Array;
import java.util.Arrays;

@Controller
public class MainController {
    @GetMapping("/")
    public String root() {
        return "redirect:/notice/list";
    }
}
