package com.apartment.apart.global.rq;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final UserService userService;
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final HttpSession session;
    private final User user;
    private SiteUser siteUser = null; // 레이지 로딩, 처음부터 넣지 않고, 요청이 들어올 때 넣는다.

    public Rq(UserService memberService, HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
        this.userService = memberService;
        this.req = req;
        this.resp = resp;
        this.session = session;

        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            this.user = (User) authentication.getPrincipal();
        } else {
            this.user = null;
        }
    }

    // 로그인 되어 있는지 체크
    public boolean isLogin() {
        return user != null;
    }

    // 로그아웃 되어 있는지 체크
    public boolean isLogout() {
        return !isLogin();
    }

    // 로그인 된 회원의 객체
    public SiteUser getSiteUser() {
        if (isLogout()) return null;

        // 데이터가 없는지 체크
//        if (siteUser == null) {
//            siteUser = userService.findByUserId(siteUser.getUserId()).orElseThrow();
//        }

        return siteUser;
    }
}
