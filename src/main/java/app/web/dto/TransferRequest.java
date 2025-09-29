package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    @NotNull
    private UUID walletId;

    @NotBlank
    private String recipientUsername;

    @NotNull
    @Positive
    private BigDecimal amount;
}
