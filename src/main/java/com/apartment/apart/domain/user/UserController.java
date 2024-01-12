package com.apartment.apart.domain.user;

import com.apartment.apart.domain.email.EmailService;
import com.apartment.apart.domain.notice.Notice;
import com.apartment.apart.domain.notice.NoticeForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.util.StringUtils;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "user/signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/signup_form";
        }
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
            return "user/signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getNickname(), userCreateForm.getPassword1(), userCreateForm.getPhone(), userCreateForm.getEmail(), userCreateForm.getApartDong(), userCreateForm.getApartHo(), false,true);

            emailService.send(userCreateForm.getEmail(), "서비스 가입을 환영합니다.", "회원가입 환영 메일");

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "user/signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "user/signup_form";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String mypage(UserMypageForm userMypageForm, UserCreateForm userCreateForm, Principal principal) {
        String username = principal.getName();
        SiteUser siteUser = this.userService.getUser(username);
        if (!siteUser.getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        userCreateForm.setUsername(siteUser.getUserId());
        userMypageForm.setNickname(siteUser.getNickname());
        userMypageForm.setPhone(siteUser.getPhone());
        userMypageForm.setEmail(siteUser.getEmail());
        userMypageForm.setApartDong(siteUser.getApartDong());
        userMypageForm.setApartHo(siteUser.getApartHo());
        return "user/mypage_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/mypage")
    public String userModify(@Valid UserMypageForm userMypageForm,
                             BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "user/mypage_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (!siteUser.getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        try {
            userService.modify(siteUser, userMypageForm.getNickname(), userMypageForm.getPassword1(),
                    userMypageForm.getPhone(), userMypageForm.getEmail(), userMypageForm.getApartDong(),
                    userMypageForm.getApartHo());
            return "redirect:/user/showmypage";
        } catch (Exception e) {
            e.printStackTrace();
            return "user/mypage_form";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/showmypage")
    public String showmypage(UserMypageForm userMypageForm, UserCreateForm userCreateForm, BindingResult bindingResult, Principal principal) {
        String username = principal.getName();
        SiteUser siteUser = this.userService.getUser(username);
        if (!siteUser.getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        userCreateForm.setUsername(siteUser.getUserId());
        userMypageForm.setNickname(siteUser.getNickname());
        userMypageForm.setPhone(siteUser.getPhone());
        userMypageForm.setEmail(siteUser.getEmail());
        userMypageForm.setApartDong(siteUser.getApartDong());
        userMypageForm.setApartHo(siteUser.getApartHo());

        if (!StringUtils.equals(userMypageForm.getPassword1(), userMypageForm.getPassword2())) {
            bindingResult.rejectValue("password2", "error.passwordMismatch", "비밀번호가 일치하지 않습니다.");
            return "user/mypage_form";
        }

        return "user/mypage_detail";
    }
}