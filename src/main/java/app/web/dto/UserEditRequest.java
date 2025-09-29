package app.web.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {

    private String firstName;

    private String lastName;

    @Email(message = "The given email has incorrect format")
    private String email;

    @URL(message = "The given picture link has incorrect format")
    private String profilePictureUrl;
}
