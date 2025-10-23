package app.web;

import app.security.UserData;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String getWallets(@AuthenticationPrincipal UserData userData) {

        return "wallets";
    }
}
