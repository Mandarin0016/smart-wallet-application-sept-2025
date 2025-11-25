package app.web;

import app.security.UserData;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    // Scenario 1: User with Admin role
    // PATCH /users/{userId}/status
    // Result:
    // - status code is 302 OK
    // - redirect /users
    // - verify switchStatus method of userService is invoked
    @Test
    void patchRequestToChangeUserStatus_fromAdminUser_shouldReturnRedirectAndInvokeServiceMethod() throws Exception {

        UserDetails authentication = adminAuthentication();
        MockHttpServletRequestBuilder httpRequest = patch("/users/{userId}/status", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        verify(userService).switchStatus(any());
    }

    // Scenario 2: User with User role
    // PATCH /users/{userId}/status
    // Result:
    // - status code is 404
    // - view name is "not-found"
    @Test
    void patchRequestToChangeUserStatus_fromNormalUser_shouldReturn404StatusCodeAndViewNotFound() throws Exception {

        UserDetails authentication = normalUserAuthentication();
        MockHttpServletRequestBuilder httpRequest = patch("/users/{userId}/status", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
        verifyNoInteractions(userService);
    }

    public static UserDetails adminAuthentication() {

        return new UserData(UUID.randomUUID(), "Vik123", "123123", UserRole.ADMIN, null, true);
    }

    public static UserDetails normalUserAuthentication() {

        return new UserData(UUID.randomUUID(), "Vik123", "123123", UserRole.USER, null, true);
    }
}
