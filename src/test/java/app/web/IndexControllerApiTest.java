package app.web;

import app.security.UserData;
import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionStatus;
import app.subscription.model.SubscriptionType;
import app.user.model.Country;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.property.UserProperties;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserProperties userProperties;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<RegisterRequest> registerRequestArgumentCaptor;

    // GET /
    // Result:
    // - status code 200 OK
    // - view "index"
    @Test
    void getIndexEndpoint_shouldReturn200OkAndIndexView() throws Exception {

        // 1. Build the HTTP request
        MockHttpServletRequestBuilder httpRequest = get("/");

        // 2. Use MockMvc to perform the request
        // 3. Assert the result
        mockMvc.perform(httpRequest)
                .andExpect(view().name("index"))
//                .andExpect(status().is(200))
                .andExpect(status().isOk());
    }

    // POST /register
    // Result:
    // - status code = 200 OK
    // - redirect to /login\
    // - verify(userService).register(any())
    @Test
    void postRegister_shouldReturn302RedirectAndRedirectToLoginAndInvokeRegisterServiceMethod() throws Exception {

        // 1. Build the HTTP request
        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "Vik1234")
                .formField("password", "123456")
                .formField("country", "BULGARIA")
                .with(csrf());

        // 2. Use MockMvc to perform the request
        // 3. Assert the result
        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successfulRegistration"));

        verify(userService).register(registerRequestArgumentCaptor.capture());

        RegisterRequest dto = registerRequestArgumentCaptor.getValue();
        assertEquals("Vik1234", dto.getUsername());
        assertEquals("123456", dto.getPassword());
        assertEquals(Country.BULGARIA, dto.getCountry());
    }

    // POST /register
    // Result:
    // - status code = 200 OK
    // - redirect to /login\
    // - verify(userService).register(any())
    @Test
    void postRegisterWithInvalidFormData_shouldReturn200OkAndShowRegisterViewAndRegisterServiceMethodIsNeverInvoked() throws Exception {

        // 1. Build the HTTP request
        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "V")
                .formField("password", "")
                .formField("country", "BULGARIA")
                .with(csrf());

        // 2. Use MockMvc to perform the request
        // 3. Assert the result
        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
//        verifyNoInteractions(userService);
        verify(userService, never()).register(any());
    }

    // GET /home
    // Result:
    // - status code 200
    // - view name home
    // - model attributes exists (user, primaryWallet)
    @Test
    void getHomePage_shouldReturnHomeViewWithUserModelAttributeAndStatusCodeIs200() throws Exception {

        // 1. Build the HTTP request
        User user = aRandomUser();
        when(userService.getById(any())).thenReturn(user);

        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.getEmail(), user.isActive());
        MockHttpServletRequestBuilder httpRequest = get("/home")
                .with(user(authentication));

        // 2. Invoke the http request
        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user", "primaryWallet"));
    }

    @Test
    void getHomePageSomethingWentWrongInTheServiceLayer_shouldReturnInternalServerErrorView() throws Exception {

        // 1. Build the HTTP request
        User user = aRandomUser();
        when(userService.getById(any())).thenThrow(RuntimeException.class);

        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.getEmail(), user.isActive());
        MockHttpServletRequestBuilder httpRequest = get("/home")
                .with(user(authentication));

        // 2. Invoke the http request
        mockMvc.perform(httpRequest)
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("internal-server-error"));
    }

    public static User aRandomUser() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("Vik123")
                .password("123123")
                .role(UserRole.USER)
                .country(Country.BULGARIA)
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        Wallet wallet = Wallet.builder()
                .owner(user)
                .id(UUID.randomUUID())
                .nickname("CoolWallet")
                .status(WalletStatus.ACTIVE)
                .main(true)
                .currency(Currency.getInstance("EUR"))
                .balance(BigDecimal.TEN)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        Subscription subscription = Subscription.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .period(SubscriptionPeriod.MONTHLY)
                .type(SubscriptionType.PREMIUM)
                .price(BigDecimal.ZERO)
                .renewalAllowed(true)
                .createdOn(LocalDateTime.now())
                .expiryOn(LocalDateTime.now().plusMonths(2))
                .build();

        user.setWallets(List.of(wallet));
        user.setSubscriptions(List.of(subscription));

        return user;
    }
}