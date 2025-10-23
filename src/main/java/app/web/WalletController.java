package app.web;

import app.security.UserData;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.WalletUtils;
import app.wallet.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final UserService userService;
    private final WalletService walletService;

    public WalletController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping
    public ModelAndView getWallets(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getUserId());
        Map<UUID, List<Transaction>> transactionsByWalletId = walletService.getLastFourTransactions(user.getWallets());

        ModelAndView modelAndView = new ModelAndView("wallets");
        modelAndView.addObject("user", user);
        modelAndView.addObject("isEligibleToUnlock", WalletUtils.isEligibleToUnlockNewWallet(user));
        modelAndView.addObject("transactionsByWalletId", transactionsByWalletId);

        return modelAndView;
    }

    @PostMapping
    public String unlock(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getUserId());
        walletService.unlockNewWallet(user);

        return "redirect:/wallets";
    }

    @PatchMapping("/{id}/balance")
    public String topUp(@PathVariable UUID id) {

        Transaction transaction = walletService.topUp(id);

        return "redirect:/transactions/" + transaction.getId();
    }

    @PatchMapping("/{id}/status")
    public String changeStatus(@PathVariable UUID id) {

        walletService.changeStatus(id);

        return "redirect:/wallets";
    }

    @PatchMapping("/{id}/primary")
    public String promoteToPrimary(@PathVariable UUID id) {

        walletService.promoteToPrimary(id);

        return "redirect:/wallets";
    }
}
