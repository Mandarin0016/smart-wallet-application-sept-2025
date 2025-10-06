package app.web.dto;

import app.subscription.model.SubscriptionPeriod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpgradeRequest {

    @NotNull
    private SubscriptionPeriod period;

    @NotNull
    private UUID walletId;
}
