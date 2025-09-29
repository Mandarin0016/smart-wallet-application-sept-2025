package app.web.dto;

import app.user.model.User;
import lombok.experimental.UtilityClass;

// Mapper = converter of objects of type A to objects of type B
@UtilityClass
public class DtoMapper {

    // Converts User object towards EditProfileRequest object
    public static EditProfileRequest fromUser(User user) {

        return EditProfileRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePicture())
                .build();
    }
}
