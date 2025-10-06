package app.web;

import app.subscription.model.SubscriptionType;
import app.subscription.service.SubscriptionService;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UpgradeRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ModelAndView getUpgradePage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upgrade");
        modelAndView.addObject("user", user);
        modelAndView.addObject("upgradeRequest", new UpgradeRequest());

        return modelAndView;
    }

    // /subscriptions/history
    @GetMapping("/history")
    public ModelAndView getSubscriptionHistoryPage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("subscription-history");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping
    private ModelAndView upgrade(@Valid UpgradeRequest upgradeRequest, BindingResult bindingResult, HttpSession session, @RequestParam("subscriptionType") SubscriptionType subscriptionType) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = userService.getById(userId);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("upgrade");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        Transaction transaction = subscriptionService.upgrade(user, upgradeRequest, subscriptionType);

        return new ModelAndView("redirect:/transactions/" + transaction.getId());
    }
}
