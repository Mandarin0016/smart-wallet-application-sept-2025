package app.web;

import app.user.model.User;
import app.user.property.UserProperties;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    private final UserService userService;
    private final UserProperties userProperties;

    @Autowired
    public IndexController(UserService userService, UserProperties userProperties) {
        this.userService = userService;
        this.userProperties = userProperties;
    }

    @GetMapping("/")
    public String getIndexPage() {

        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(name = "loginAttemptMessage", required = false) String message) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());
        modelAndView.addObject("loginAttemptMessage", message);

        return modelAndView;
    }

    // Autowire HttpSession = automatically create user session, generate session id and return Set-Cookie header with the session id
    @PostMapping("/login")
    public ModelAndView login(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("login");
        }

        User user = userService.login(loginRequest);
        session.setAttribute("userId", user.getId());
        return new ModelAndView("redirect:/home");
    }

    // Form Handling steps:
    // 1. Return HTML form with empty object
    // 2. Use this empty object in the html form to fill the data
    // 3. Get the object filled with data via POST request
    // 4. Validate the received object - @Valid
    // 5. Capture all the validation errors if any exist - BindingResult
    // 6. Check if there are validation errors - if (bindingResult.hasErrors())
    //  - If there are errors -> show the same page and visualize errors - th:if="${#fields.hasErrors('username')}" th:errors="*{username}"
    //  - If there are not errors, display the next page ->

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    // After POST, PUT, PATCH, DELETE requests we do "redirect:/endpoint"
    // Redirect = tells the client where to send the GET request
    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerRequest);
        redirectAttributes.addFlashAttribute("successfulRegistration", "You have registered successfully");

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/";
    }
}
