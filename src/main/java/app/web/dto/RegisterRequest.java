package app.web.dto;

import app.user.model.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 6, max = 26, message = "Username length must be between 6 and 26 symbols.")
    private String username;
    @NotBlank
    @Size(min = 6, max = 6, message = "Password must be exactly 6 symbols.")
    private String password;
    @NotNull
    private Country country;
}
