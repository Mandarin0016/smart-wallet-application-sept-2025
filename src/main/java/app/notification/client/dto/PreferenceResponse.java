package app.notification.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreferenceResponse {

    private boolean notificationEnabled;

    private String contactInfo;
}
