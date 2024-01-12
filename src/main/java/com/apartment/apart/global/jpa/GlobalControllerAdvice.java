package com.apartment.apart.global.jpa;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;

    @ModelAttribute
    public String globalAttributes(Model model, HttpServletRequest request, Principal principal) {
        if (model.containsAttribute("request")) {
            return "";
        }

        model.addAttribute("request", request);

        if (principal != null) {
            SiteUser loginUser = this.userService.getUser(principal.getName());

            if (loginUser != null) {
                if (!model.containsAttribute("loginUser")) {
                    model.addAttribute("loginUser", loginUser);
                } else {
                    return "redirect:/";
                }
            }
        }
        return "";
    }
}

