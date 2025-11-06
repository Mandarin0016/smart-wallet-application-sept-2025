package app.notification.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpsertPreferenceRequest {

    private UUID userId;

    private boolean notificationEnabled;

    private String contactInfo;
}
