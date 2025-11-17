package app.user;

import app.exception.UserNotFoundException;
import app.notification.service.NotificationService;
import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import app.web.dto.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    // 1. Mock all dependencies
    // 2. Inject all mocks
    // 3. Think of a scenario to test

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private WalletService walletService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private UserProperties userProperties;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserService userService;


    // 1. Ако няма потребител тогава се хвърля exception
    @Test
    void whenEditUserDetails_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        // Given
        UUID userId = UUID.randomUUID();
        EditProfileRequest dto = null;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(userId, dto));
    }

    // 2. Ако има потребител се обновяват данните му и се запазва обратно в базата
    @Test
    void whenEditUserDetails_andRepositoryReturnsUserFromTheDatabase_thenUpdateTheUserDetailsAndSaveItToTheDatabase() {

        // Given
        UUID userId = UUID.randomUUID();
        EditProfileRequest dto = EditProfileRequest.builder()
                .firstName("Gosho")
                .lastName("Georgiev")
                .profilePictureUrl("www.picture.com")
                .email("joro@gmail.com")
                .build();
        User userRetrievedFromDatabase = User.builder()
                .id(userId)
                .firstName("Vik")
                .lastName("Aleksandrov")
                .profilePicture(null)
                .email("vik@gmail.com")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(userRetrievedFromDatabase));

        // When
        userService.updateProfile(userId, dto);

        // Then
        assertEquals("Gosho", userRetrievedFromDatabase.getFirstName());
        assertEquals("Georgiev", userRetrievedFromDatabase.getLastName());
        assertNotNull(userRetrievedFromDatabase.getProfilePicture());
        assertEquals("www.picture.com", userRetrievedFromDatabase.getProfilePicture());
        assertEquals("joro@gmail.com", userRetrievedFromDatabase.getEmail());
        verify(userRepository).save(userRetrievedFromDatabase);
    }

    // 3. Ако има потребител и dto-то идва с имейл се извиква upsertPreference с true
    @Test
    void whenEditUserDetails_andRepositoiryReturnsUserAndDtoComesWithNonEmptyEmail_thenInvokeUpsertNotificationPreferenceWithTrue() {

        // Given
        UUID userId = UUID.randomUUID();
        EditProfileRequest dto = EditProfileRequest.builder()
                .email("joro@gmail.com")
                .build();
        User user = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // When
        userService.updateProfile(userId, dto);

        // Then
        verify(notificationService).upsertPreference(userId, true, "joro@gmail.com");
    }

    // 4. Ако има потребител и dto-то идва с празен имейл се извиква upsertPreference с false
    @Test
    void whenEditUserDetails_andRepositoiryReturnsUserAndDtoComesWithEmptyEmail_thenInvokeUpsertNotificationPreferenceWithFalse() {

        // Given
        UUID userId = UUID.randomUUID();
        EditProfileRequest dto = EditProfileRequest.builder()
                .email(null)
                .build();
        User user = User.builder()
                .id(userId)
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // When
        userService.updateProfile(userId, dto);

        // Then
        // verify - вярно ли е че
        verify(notificationService).upsertPreference(userId, false, null);
    }

    // Test method: switchRole(UUID userId)
    // 1. Aко потребителя от базата е Admin, неговата роля става на User и се запазва отново в базата
    // 2. Aко потребителя от базата е User, неговата роля става на Admin и се запазва отново в базата
    // 3. Ако няма потребител - хвърля се грешка

    @Test
    void whenSwitchRole_andRepositoryReturnsAdmin_thenUserIsUpdatedWithRoleUserAndUpdatedOnNow_andPersistedInTheDatabase() {

        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.switchRole(userId);

        // Then
        assertEquals(UserRole.USER, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsUser_thenUserIsUpdatedWithRoleAdminAndUpdatedOnNow_andPersistedInTheDatabase() {

        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.USER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.switchRole(userId);

        // Then
        assertEquals(UserRole.ADMIN, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        assertThrows(UserNotFoundException.class, () -> userService.switchRole(userId));
    }
}
