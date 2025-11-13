package app.notification.service;

import app.exception.NotificationRetryFailedException;
import app.notification.client.NotificationClient;
import app.notification.client.dto.Email;
import app.notification.client.dto.EmailRequest;
import app.notification.client.dto.PreferenceResponse;
import app.notification.client.dto.UpsertPreferenceRequest;
import app.web.dto.NotificationPreferenceState;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static app.web.dto.NotificationPreferenceState.OFF;

@Slf4j
@Service
public class NotificationService {

    private final NotificationClient client;

    @Autowired
    public NotificationService(NotificationClient client) {
        this.client = client;
    }

    public void upsertPreference(UUID userId, boolean notificationsEnabled, String email) {

        UpsertPreferenceRequest dto = UpsertPreferenceRequest.builder()
                .userId(userId)
                .notificationEnabled(notificationsEnabled)
                .contactInfo(email)
                .build();

        try {
            // Feign client - той може да изпраща HTTP заявка
            // HTTP заявката ще получи HTTP отговор
            // HTTP отговор идва със статус код
            // Какъв е статус кода?
            // Ако статус кода е 200-299 - тогава Feign библиотеката няма да хвърли exception
            // Ако статус кода e друг - тогава Feign библиотеката хвърля FeignException
            // Какво ще направя, ако библиотеката ми хвърли FeignException?
            // Трябва да го catch-на
            // Когато го catch-на трябва да реша какво да правя с него?
            client.upsertPreference(dto);
        } catch (FeignException e) {
            // Вариант 1: логвам съобщение за да може програмиста да инвестигира и да разбере причаната за проблем
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            // Вариант 2: хвърлям грешка и прекъсвам основната операция (register)
            // Вариант 3: запазвам audit log
        }
    }

    public PreferenceResponse getPreferenceByUserId(UUID userId) {

        return client.getPreferenceByUserId(userId).getBody();
    }

    public List<Email> getUserLastEmails(UUID userId) {

        ResponseEntity<List<Email>> response = client.getNotificationHistory(userId);

        return response.getBody() != null
                ? response.getBody().stream().limit(5).toList()
                : Collections.emptyList();
    }

    public void sendEmail(UUID userId, String subject, String body) {

        EmailRequest dto = EmailRequest.builder()
                .userId(userId)
                .subject(subject)
                .body(body)
                .build();

        try {
            client.sendEmail(dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }

    public void updatePreferenceState(NotificationPreferenceState state, UUID userId, String email) {

        UpsertPreferenceRequest dto = UpsertPreferenceRequest.builder()
                .userId(userId)
                .contactInfo(email)
                .build();

        if (state == OFF) {
            dto.setNotificationEnabled(false);
        } else {
            dto.setNotificationEnabled(true);
        }

        try {
            client.upsertPreference(dto);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }

    public void deleteAllEmails(UUID userId) {

        try {
            client.deleteAllNotifications(userId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
        }
    }

    public void retryFailedEmails(UUID userId) {

        try {
            client.retryFailed(userId);
        } catch (FeignException e) {
            log.error("[S2S Call]: Failed due to %s.".formatted(e.getMessage()));
            // What is Feign ErrorDecoder and how to implement one?
            if (e.status() == HttpStatus.FORBIDDEN.value()) {
                throw new NotificationRetryFailedException("Failed to retry emails, try again later.");
            } else {
                throw new RuntimeException("notification-svc is down");
            }
        }
    }
}
