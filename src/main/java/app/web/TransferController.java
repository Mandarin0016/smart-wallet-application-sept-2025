package app.web;

import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import app.web.dto.TransferRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    private final WalletService walletService;
    private final UserService userService;

    @Autowired
    public TransferController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getTransferPage() {

        User user = userService.getDefaultUser();

        ModelAndView modelAndView = new ModelAndView("transfer");
        modelAndView.addObject("user", user);
        modelAndView.addObject("transferRequest", new TransferRequest());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView transfer(@Valid TransferRequest transferRequest, BindingResult bindingResult) {

        User user = userService.getDefaultUser();

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("transfer");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        Transaction transaction = walletService.transfer(transferRequest);

        return new ModelAndView("redirect:/transactions/" + transaction.getId());
    }
}
