package app.web;

import app.user.model.User;
import app.web.dto.DtoMapper;
import app.web.dto.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void fromUserToEditProfileRequest_whenUserWithDetailsIsPassed_thenDtoIsReturnedWithSameDetails() {

        // Given
        User user = User.builder()
                .firstName("Vik")
                .lastName("Aleksandrov")
                .email("vik@gmail.com")
                .profilePicture("picture.png")
                .build();

        // When
        EditProfileRequest result = DtoMapper.fromUser(user);

        // Then
        assertEquals("Vik", result.getFirstName());
        assertEquals("Aleksandrov", result.getLastName());
        assertEquals("vik@gmail.com", result.getEmail());
        assertEquals("picture.png", result.getProfilePictureUrl());
    }
}
