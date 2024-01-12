package com.apartment.apart.domain.communityReply;

import com.apartment.apart.domain.community.Community;
import com.apartment.apart.domain.community.CommunityService;
import com.apartment.apart.domain.user.SiteUser;
import com.apartment.apart.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/communityReply")
@RequiredArgsConstructor
@Controller
public class CommunityReplyController {
    private final CommunityService communityService;
    private final CommunityReplyService communityReplyService;
    private final UserService userService;
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String create(Model model, @PathVariable("id") Long id,
                               @Valid CommunityReplyForm communityReplyForm, BindingResult bindingResult, Principal principal) {

        //답변 부모 질문객체를 받아온다.
        Community community = this.communityService.getCommunity(id);
        SiteUser user = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("community", community);
            return "community_detail";
        }

        CommunityReply communityReply = this.communityReplyService.create(community, communityReplyForm.getContent(), user);

        return "redirect:/community/detail/%d#communityReply_%s".formatted(id, communityReply.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String communityReplyModify(Model model, @PathVariable("id") Long id, Principal principal) {
        CommunityReply communityReply = this.communityReplyService.getCommunityReply(id);
        if (!communityReply.getUser().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        CommunityReplyForm communityReplyForm = new CommunityReplyForm();
        communityReplyForm.setContent(communityReply.getContent());

        model.addAttribute("communityReplyForm", communityReplyForm); // 수정 폼에 초기값으로 내용을 설정
        model.addAttribute("communityReplyId", id); // 수정 대상의 ID를 모델에 추가

        return "community_reply_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String communityReplyModify(@Valid CommunityReplyForm communityReplyForm, BindingResult bindingResult,
                                       @PathVariable("id") Long id, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            return "communityReply_form";
        }

        CommunityReply communityReply = this.communityReplyService.getCommunityReply(id);
        if (!communityReply.getUser().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.communityReplyService.modify(communityReply, communityReplyForm.getContent());
        return String.format("redirect:/community/detail/%s", communityReply.getCommunity().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String communityReplyDelete(Principal principal, @PathVariable("id") Long id) {
        CommunityReply communityReply = this.communityReplyService.getCommunityReply(id);
        SiteUser user = this.userService.getUser(principal.getName());

        if (!communityReply.getUser().getUserId().equals(user.getUserId()) && !user.isCheckedAdmin()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.communityReplyService.delete(communityReply);
        return String.format("redirect:/community/detail/%s", communityReply.getCommunity().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like/{id}")
    @ResponseBody
    public String communityReplyLike(Principal principal, @PathVariable("id") Long id) {
        CommunityReply communityReply = this.communityReplyService.getCommunityReply(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.communityReplyService.like(communityReply, siteUser);

        CommunityReply likedCommunityReply = this.communityReplyService.getCommunityReply(id);

        Integer count = likedCommunityReply.getLikeCount().size();
        return count.toString();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unlike/{id}")
    @ResponseBody
    public String communityReplyUnlike(Principal principal, @PathVariable("id") Long id) {
        CommunityReply communityReply = this.communityReplyService.getCommunityReply(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        try {
            this.communityReplyService.cancelLike(communityReply, siteUser);

            CommunityReply unlikedCommunityReply = this.communityReplyService.getCommunityReply(id);
            Integer count = unlikedCommunityReply.getLikeCount().size();

            return count.toString();
        } catch (IllegalStateException e) {
            return "이미 추천을 취소한 상태이거나 추천을 하지 않은 경우입니다.";
        }
    }
}