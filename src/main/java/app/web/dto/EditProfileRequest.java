package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {

    @Size(min = 2, max = 24)
    private String firstName;

    @Size(min = 2, max = 24)
    private String lastName;

    @Email
    private String email;

    @URL
    private String profilePictureUrl;
}
