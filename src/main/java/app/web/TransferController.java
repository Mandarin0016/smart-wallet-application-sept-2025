package app.web;

import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import app.web.dto.TransferRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    private final UserService userService;
    private final WalletService walletService;

    @Autowired
    public TransferController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping
    public ModelAndView getTransferPage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transfer");
        modelAndView.addObject("transferRequest", new TransferRequest());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping
    public ModelAndView transferMoney(@Valid TransferRequest transferRequest, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            UUID userId = (UUID) session.getAttribute("userId");
            User user = userService.getById(userId);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("transfer");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        Transaction transaction = walletService.transfer(transferRequest);

        // GET /transactions/{id}
        return new ModelAndView("redirect:/transactions/" + transaction.getId());
    }
}
