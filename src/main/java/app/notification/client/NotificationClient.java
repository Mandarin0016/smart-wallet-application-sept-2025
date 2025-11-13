package app.notification.client;

import app.notification.client.dto.Email;
import app.notification.client.dto.EmailRequest;
import app.notification.client.dto.PreferenceResponse;
import app.notification.client.dto.UpsertPreferenceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// name =  метаданни
// url =  основен URL
// Когато използвам Feign библиотеката и анотирам интерфейс с анотация @FeignClient, библиотеката ще създаде runtime
// Spring component (имплементация/обект), който да се инжектира в останалите спринг компоненти (@service, @component, @repository, @controller, @restController)
@FeignClient(name = "notification-svc", url = "http://localhost:8081/api/v1")
public interface NotificationClient {

    @PostMapping("/preferences")
    ResponseEntity<Void> upsertPreference(@RequestBody UpsertPreferenceRequest requestBody);

    // Създавам GET заявка
    @GetMapping("/preferences")
    ResponseEntity<PreferenceResponse> getPreferenceByUserId(@RequestParam("userId") UUID userId);

    @GetMapping("/notifications")
    ResponseEntity<List<Email>> getNotificationHistory(@RequestParam("userId") UUID userId);

    @PostMapping("/notifications")
    ResponseEntity<Void> sendEmail(@RequestBody EmailRequest requestBody);

    @DeleteMapping("/notifications")
    ResponseEntity<Void> deleteAllNotifications(@RequestParam("userId") UUID userId);

    @PutMapping("/notifications")
    ResponseEntity<Void> retryFailed(@RequestParam("userId") UUID userId);
}
