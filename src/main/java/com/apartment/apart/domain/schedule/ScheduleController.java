package com.apartment.apart.domain.schedule;

import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/schedule/list")
    public String list(Model model, @RequestParam(value = "targetDong", required = false) Integer targetDong, Principal principal,
                       HttpServletRequest request) {

        if (targetDong == null) {
            int userDong = this.userService.getUser(principal.getName()).getApartDong();
            List<Schedule> scheduleList = this.scheduleService.findByTargetDongAndTotal(userDong);
            List<ScheduleForm> scheduleFormList = new ArrayList<>();
            for (Schedule schedule1 : scheduleList) {
                ScheduleForm sc1 = new ScheduleForm();
                sc1.setTitle(schedule1.getTitle());
                sc1.setStart(schedule1.getStartDate().toString());
                sc1.setEnd(schedule1.getEndDate().plusDays(1).toString());
                scheduleFormList.add(sc1);
            }
            String nowDong;
            if (userDong == 100) {
                nowDong = "전체 일정";
            } else {
                nowDong = userDong + "동 일정";
            }

            model.addAttribute("scheduleList", scheduleFormList);
            model.addAttribute("nowDong", nowDong);
            model.addAttribute("request", request);
            return "schedule/schedule_list";
        }


        if (targetDong == 100) {
            List<Schedule> scheduleList = this.scheduleService.findAll();
            List<ScheduleForm> scheduleFormList = new ArrayList<>();
            for (Schedule schedule1 : scheduleList) {
                ScheduleForm sc1 = new ScheduleForm();
                sc1.setTitle(schedule1.getTitle());
                sc1.setStart(schedule1.getStartDate().toString());
                sc1.setEnd(schedule1.getEndDate().plusDays(1).toString());
                scheduleFormList.add(sc1);
            }
            String nowDong = "전체 일정";
            model.addAttribute("scheduleList", scheduleFormList);
            model.addAttribute("nowDong", nowDong);
            model.addAttribute("request", request);
            return "schedule/schedule_list";
        } else {
            List<Schedule> scheduleList = this.scheduleService.findByTargetDongAndTotal(targetDong);
            List<ScheduleForm> scheduleFormList = new ArrayList<>();
            for (Schedule schedule1 : scheduleList) {
                ScheduleForm sc1 = new ScheduleForm();
                sc1.setTitle(schedule1.getTitle());
                sc1.setStart(schedule1.getStartDate().toString());
                sc1.setEnd(schedule1.getEndDate().plusDays(1).toString());
                scheduleFormList.add(sc1);
            }

            String nowDong = targetDong + "동 일정";
            model.addAttribute("scheduleList", scheduleFormList);
            model.addAttribute("nowDong", nowDong);
            model.addAttribute("request", request);
            return "schedule/schedule_list";
        }

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/schedule/create")
    public String create(Model model,
                         HttpServletRequest request) {
        model.addAttribute("scheduleForm", new ScheduleForm());
        model.addAttribute("request", request);
        return "schedule/schedule_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/schedule/create")
    public String create(@Valid ScheduleForm scheduleForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "schedule/schedule_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        this.scheduleService.save(scheduleForm, siteUser);
        return "redirect:/schedule/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/schedule/manage")
    public String mySchedule(Model model, Principal principal,
                             HttpServletRequest request) {
        SiteUser siteUser = userService.getUser(principal.getName());
        List<Schedule> scheduleList;
        if (siteUser.isCheckedAdmin()) {
            scheduleList = this.scheduleService.findAll();
        } else {
            scheduleList = this.scheduleService.findByUser(siteUser);
        }
        model.addAttribute("mySchedule", scheduleList);
        model.addAttribute("request", request);
        return "schedule/my_schedule";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/schedule/delete/{id}")
    public String delete(Principal principal, @PathVariable("id") Long id) {
        SiteUser loginUser = this.userService.getUser(principal.getName());
        Schedule schedule = this.scheduleService.getSchedule(id);
        if (loginUser.isCheckedAdmin() || schedule.getUser().getUserId().equals(principal.getName())) {
            this.scheduleService.delete(schedule);
            return "redirect:/schedule/manage";
        } else {
            return "redirect:/schedule/list";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/schedule/modify/{id}")
    public String modify(Principal principal,
                         @PathVariable("id") Long id,
                         Model model,
                         HttpServletRequest request) {

        Schedule schedule = this.scheduleService.getSchedule(id);
        SiteUser loginUser = this.userService.getUser(principal.getName());
        if (loginUser.isCheckedAdmin() || schedule.getUser().getUserId().equals(principal.getName())) {

            ScheduleForm modifyScheduleForm = new ScheduleForm();
            modifyScheduleForm.setTitle(schedule.getTitle());
            modifyScheduleForm.setEnd(String.valueOf(schedule.getEndDate()));
            modifyScheduleForm.setStart(String.valueOf(schedule.getStartDate()));

            model.addAttribute("scheduleForm", modifyScheduleForm);
            model.addAttribute("request", request);
            return "schedule/schedule_form";
        } else {
            return "redirect:/schedule/list";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/schedule/modify/{id}")
    public String modify(@Valid ScheduleForm scheduleForm,
                         BindingResult bindingResult,
                         Principal principal,
                         @PathVariable("id") Long id) {
        this.scheduleService.modify(scheduleForm, id);
        return "redirect:/schedule/manage";
    }
}