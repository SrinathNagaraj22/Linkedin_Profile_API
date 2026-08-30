package com.tross.linkedinapi.controller;

import com.tross.linkedinapi.dto.MetadataDto;
import com.tross.linkedinapi.dto.ProfileApiResponse;
import com.tross.linkedinapi.dto.ProfileResponse;
import com.tross.linkedinapi.exception.GlobalExceptionHandler;
import com.tross.linkedinapi.exception.ProfileNotFoundException;
import com.tross.linkedinapi.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProfileController.class)
@Import(GlobalExceptionHandler.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @Test
    void returns200ForValidRequest() throws Exception {
        ProfileResponse profile = new ProfileResponse();
        profile.setName("Jane Doe");
        ProfileApiResponse apiResponse = new ProfileApiResponse(profile, new MetadataDto("linkedin", "now", false));

        when(profileService.getProfile("https://www.linkedin.com/in/jane-doe/")).thenReturn(apiResponse);

        mockMvc.perform(post("/api/v1/linkedin/profile")
                        .contentType("application/json")
                        .content("{\"profileUrl\":\"https://www.linkedin.com/in/jane-doe/\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.name").value("Jane Doe"));
    }

    @Test
    void returns400ForBlankProfileUrl() throws Exception {
        mockMvc.perform(post("/api/v1/linkedin/profile")
                        .contentType("application/json")
                        .content("{\"profileUrl\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns400ForMissingBody() throws Exception {
        mockMvc.perform(post("/api/v1/linkedin/profile")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns404WhenServiceThrowsNotFound() throws Exception {
        when(profileService.getProfile(anyString())).thenThrow(new ProfileNotFoundException("not found"));

        mockMvc.perform(post("/api/v1/linkedin/profile")
                        .contentType("application/json")
                        .content("{\"profileUrl\":\"https://www.linkedin.com/in/jane-doe/\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("PROFILE_NOT_FOUND"));
    }
}
