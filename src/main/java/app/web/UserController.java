package app.web;

import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.DtoMapper;
import app.web.dto.UserEditRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // /users/{id}/profile
    @GetMapping("/{id}/profile")
    public ModelAndView getProfilePage(@PathVariable UUID id) {

        User user = userService.getById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-menu");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditRequest", DtoMapper.fromUser(user));

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView editProfile(@PathVariable UUID id, @Valid UserEditRequest userEditRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("profile-menu");
            modelAndView.addObject("user", userService.getById(id));
            return modelAndView;
        }

        userService.editProfile(id, userEditRequest);

        return new ModelAndView("redirect:/home");
    }

    @GetMapping
    public ModelAndView getUsers() {

        List<User> users = userService.getAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }
}
