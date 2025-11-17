package app.utils;

import app.notification.client.dto.Email;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class EmailUtils {

    // 1. Ако подам списък с 2 успешни имейла и 1 неуспешен имейла, получават отговор 2
    // 2. Ако подам празен списък - отговор 0
    public static long getNonFailedEmailsCount(List<Email> emails) {

        return emails.stream().filter(e -> e.getStatus().equals("SUCCEEDED")).count();
    }

    // 1. Ако подам списък с 2 успешни имейла и 1 неуспешен имейла, получават отговор 1
    // 2. Ако подам празен списък - отговор 0
    public static long getFailedEmailsCount(List<Email> emails) {

        return emails.stream().filter(e -> e.getStatus().equals("FAILED")).count();
    }
}
