package com.tross.linkedinapi.controller;

import com.tross.linkedinapi.dto.ProfileApiResponse;
import com.tross.linkedinapi.dto.ProfileRequest;
import com.tross.linkedinapi.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/linkedin")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/profile")
    public ResponseEntity<ProfileApiResponse> getProfile(@Valid @RequestBody ProfileRequest request) {
        ProfileApiResponse response = profileService.getProfile(request.getProfileUrl());
        return ResponseEntity.ok(response);
    }
}
