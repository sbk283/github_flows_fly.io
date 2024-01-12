package com.apartment.apart.domain.community;

import com.apartment.apart.domain.communityReply.CommunityReplyForm;
import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
    private final CommunityService communityService;
    private final UserService userService;
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Community> paging = this.communityService.getList(page, kw);
        model.addAttribute("paging", paging);
        return "community_list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, CommunityReplyForm communityReplyForm) {
        Community community = this.communityService.getCommunity(id);
        model.addAttribute("community", community);
        return "community_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String communityCreate(CommunityForm communityForm) {
        return "community_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String communityCreate(@Valid CommunityForm communityForm, BindingResult bindingResult, Principal principal,  @RequestParam("thumbnail") MultipartFile thumbnail) {
        if (bindingResult.hasErrors()) {
            return "community_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.communityService.create(communityForm.getTitle(), communityForm.getContent(), siteUser, thumbnail);
        return "redirect:/community/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String communityModify(CommunityForm communityForm, @PathVariable("id") Long id, Principal principal) {
        Community community = this.communityService.getCommunity(id);
        if(!community.getUser().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        communityForm.setTitle(community.getTitle());
        communityForm.setContent(community.getContent());
        return "community_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String communityModify(@Valid CommunityForm communityForm, BindingResult bindingResult,
                                  Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "community_form";
        }
        Community community = this.communityService.getCommunity(id);
        if (!community.getUser().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.communityService.modify(community, communityForm.getTitle(), communityForm.getContent());
        return String.format("redirect:/community/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String communityDelete(Principal principal, @PathVariable("id") Long id) {
        Community community = this.communityService.getCommunity(id);
        SiteUser user = this.userService.getUser(principal.getName());

        // 사용자가 게시물의 작성자이거나 관리자인지 확인
        if (!community.getUser().getUserId().equals(user.getUserId()) && !user.isCheckedAdmin()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.communityService.delete(community);
        return "redirect:/community/list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like/{id}")
    @ResponseBody
    public String communityLike(Principal principal, @PathVariable("id") Long id) {
        Community community = this.communityService.getCommunity(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.communityService.like(community, siteUser);

        Community likedCommunity = this.communityService.getCommunity(id);
        Integer count = likedCommunity.getLikeCount().size();

        return count.toString();
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unlike/{id}")
    @ResponseBody
    public String communityUnlike(Principal principal, @PathVariable("id") Long id) {
        Community community = this.communityService.getCommunity(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        try {
            this.communityService.cancelLike(community, siteUser);

            Community unlikedCommunity = this.communityService.getCommunity(id);
            Integer count = unlikedCommunity.getLikeCount().size();

            return count.toString();
        } catch (IllegalStateException e) {
            return "이미 추천을 취소한 상태이거나 추천을 하지 않은 경우입니다.";
        }
    }
}