package app.utility;

import app.notification.client.dto.Email;
import app.utils.EmailUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EmailUtilityUTest {

    // 1. Ако подам списък с 2 успешни имейла и 1 неуспешен имейл, получават отговор 2
    @Test
    void getNonFailedEmailsCount_whenPassListOf2SucceededAnd1FailedEmails_thenReturn2() {

        // Given
        Email oneSucceeded = Email.builder().status("SUCCEEDED").build();
        Email twoSucceeded = Email.builder().status("SUCCEEDED").build();
        Email threeFailed = Email.builder().status("FAILED").build();

        List<Email> emails = List.of(oneSucceeded, twoSucceeded, threeFailed);

        // When
        long result = EmailUtils.getNonFailedEmailsCount(emails);

        // Then
        assertEquals(2, result);
    }

    // 2. Ако подам празен списък - отговор 0
    @Test
    void getNonFailedEmailsCount_whenPassEmptyList_thenReturn0() {

        // Given & When
        long result = EmailUtils.getNonFailedEmailsCount(List.of());

        // Then
        assertEquals(0, result);
    }

    // 1. Ако подам списък с 2 успешни имейла и 1 неуспешен имейла, получават отговор 1
    @Test
    void getFailedEmailsCount_whenPassListOf2SucceededAnd1FailedEmails_thenReturn1() {

        // Given
        Email oneSucceeded = Email.builder().status("SUCCEEDED").build();
        Email twoSucceeded = Email.builder().status("SUCCEEDED").build();
        Email threeFailed = Email.builder().status("FAILED").build();

        List<Email> emails = List.of(oneSucceeded, twoSucceeded, threeFailed);

        // When
        long result = EmailUtils.getFailedEmailsCount(emails);

        // Then
        assertEquals(1, result);
    }

    // 2. Ако подам празен списък - отговор 0
    @Test
    void getFailedEmailsCount_whenPassEmptyList_thenReturn0() {

        // Given & When
        long result = EmailUtils.getFailedEmailsCount(List.of());

        // Then
        assertEquals(0, result);
    }
}
