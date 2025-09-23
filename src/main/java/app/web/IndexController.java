package app.web;

import app.user.model.User;
import app.user.property.UserProperties;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

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
    public String getLoginPage() {

        return "login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {

        return "register";
    }

    @GetMapping("/home")
    public ModelAndView getHomePage() {

        User user = userService.getByUsername(userProperties.getDefaultUser().getUsername());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
