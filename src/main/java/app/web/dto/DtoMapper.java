package app.web.dto;

import app.user.model.User;
import lombok.experimental.UtilityClass;

// Mapper class = class that maps object "A" to object "B"
@UtilityClass
public class DtoMapper {

    public static UserEditRequest fromUser(User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePictureUrl(user.getProfilePicture())
                .email(user.getEmail())
                .build();
    }
}
