package app.user.service;

import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.wallet.service.WalletService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletService walletService, SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
    }

    public User login(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Incorrect username or password.");
        }

        String rawPassword = loginRequest.getPassword();
        String hashedPassword = optionalUser.get().getPassword();
        if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
            throw new RuntimeException("Incorrect username or password.");
        }

        return optionalUser.get();
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());
        if (optionalUser.isPresent()) {
            throw new RuntimeException("User with [%s] username already exist.".formatted(registerRequest.getUsername()));
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .country(registerRequest.getCountry())
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        user = userRepository.save(user);
        walletService.createDefaultWallet(user);
        subscriptionService.createDefaultSubscription(user);

        log.info("New user profile was registered in the system for user [%s].".formatted(registerRequest.getUsername()));
    }

    public List<User> getAll() {

        return userRepository.findAll();
    }

    public User getByUsername(String username) {

        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User with [%s] username does not exist.".formatted(username)));
    }

    public User getById(UUID id) {

        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with [%s] id does not exist.".formatted(id)));
    }
}
