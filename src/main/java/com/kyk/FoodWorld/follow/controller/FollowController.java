package com.kyk.FoodWorld.follow.controller;


import com.kyk.FoodWorld.follow.service.FollowServiceImpl;
import com.kyk.FoodWorld.member.domain.LoginSessionConst;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowServiceImpl followService;


    /**
     * 팔로우 / 언팔로우
     */
    @GetMapping("/members/follow/{toMemberId}")
    public String followUpdate(@PathVariable Long toMemberId,
                               @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                               RedirectAttributes redirectAttributes) {

        followService.followAndUnFollow(loginMember.getId(), toMemberId);

        redirectAttributes.addAttribute("toMemberId", toMemberId);

        return "redirect:/members/profile/{toMemberId}";
    }

}
