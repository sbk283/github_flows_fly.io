package com.apartment.apart.domain.vote;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/list")
    public String list(Model model,
                       Principal principal,
                       @RequestParam(value = "type", defaultValue = "list") String type,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "status", defaultValue = "") String status,
                       HttpServletRequest request) {
        if (type.equals("list")) {
            if (status.isEmpty()) {
                status = "total";
            }
            Page<Vote> paging = this.voteService.getPageList(page, kw, status);
            SiteUser loginUser = this.userService.getUser(principal.getName());

            String nowStatus = switch (status) {
                case "total" -> "전체";
                case "inProgress" -> "진행중";
                case "closed" -> "지난 투표";
                case "intended" -> "투표 예정";
                default -> "";
            };
            model.addAttribute("paging", paging);
            model.addAttribute("type", type);
            model.addAttribute("request", request);
            model.addAttribute("kw", kw);
            model.addAttribute("status", nowStatus);
            model.addAttribute("loginUser", loginUser);

            return "vote/vote_list_list";
        } else if (type.equals("card")) {

            if (status.isEmpty()) {
                status = "inProgress";
            }
            List<Vote> voteList = voteService.findAll(kw, status);
            Collections.reverse(voteList);
            LocalDate today = LocalDate.now();
            SiteUser loginUser = this.userService.getUser(principal.getName());

            String nowStatus = switch (status) {
                case "total" -> "전체";
                case "inProgress" -> "진행중";
                case "closed" -> "지난 투표";
                case "intended" -> "투표 예정";
                default -> "";
            };

            model.addAttribute("voteList", voteList);
            model.addAttribute("today", today);
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("type", type);
            model.addAttribute("request", request);
            model.addAttribute("status", nowStatus);

            return "vote/vote_list_card";
        } else {
            return "redirect:/";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/create")
    public String create(Model model,
                         HttpServletRequest request,
                         Principal principal) {

        SiteUser loginUser = this.userService.getUser(principal.getName());

        if (loginUser.isCheckedAdmin()) {
            model.addAttribute("voteForm", new VoteForm());
            model.addAttribute("request", request);
            return "vote/vote_form";
        }
        return "redirect:/vote/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote/create")
    public String create(@Valid VoteForm voteForm, BindingResult bindingResult, Principal principal,
                         HttpServletRequest request, Model model) {

        SiteUser loginUser = userService.getUser(principal.getName());
        if (loginUser.isCheckedAdmin()) {
            if (bindingResult.hasErrors()) {

                model.addAttribute("request", request);
                return "vote/vote_form";
            }
            this.voteService.save(voteForm, loginUser);
            return "redirect:/vote/list";
        }
        return "redirect:/vote/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/manage")
    public String manage(Model model, Principal principal,
                         @RequestParam(value = "kw", defaultValue = "") String kw,
                         @RequestParam(value = "status", defaultValue = "") String status,
                         HttpServletRequest request) {
        SiteUser loginUser = userService.getUser(principal.getName());
        if (loginUser.isCheckedAdmin()) {
            List<Vote> votelist = this.voteService.findAll(kw, status);
            Collections.reverse(votelist);
            String nowStatus = switch (status) {
                case "total" -> "전체";
                case "inProgress" -> "진행중";
                case "closed" -> "지난 투표";
                case "intended" -> "투표 예정";
                default -> "";
            };
            model.addAttribute("status", nowStatus);
            model.addAttribute("votelist", votelist);
            model.addAttribute("request", request);
            return "vote/vote_manage";
        }
        return "redirect:/vote/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/delete/{id}")
    public String delete(Principal principal, @PathVariable("id") Long id) {

        SiteUser loginUser = userService.getUser(principal.getName());
        if (loginUser.isCheckedAdmin()) {
            Vote vote = this.voteService.findById(id);
            this.voteService.delete(vote);
            return "redirect:/vote/manage";
        }
        return "redirect:/vote/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id,
                         HttpServletRequest request,
                         Principal principal) {

        LocalDate today = LocalDate.now();
        SiteUser loginUser = this.userService.getUser(principal.getName());
        model.addAttribute("vote", this.voteService.findById(id));
        model.addAttribute("request", request);
        model.addAttribute("today", today);
        model.addAttribute("loginUser", loginUser);
        return "vote/vote_detail";
    }
}
