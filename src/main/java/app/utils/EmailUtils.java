package app.utils;

import app.notification.client.dto.Email;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class EmailUtils {

    public static long getNonFailedEmailsCount(List<Email> emails) {

        return emails.stream().filter(e -> e.getStatus().equals("SUCCEEDED")).count();
    }

    public static long getFailedEmailsCount(List<Email> emails) {

        return emails.stream().filter(e -> e.getStatus().equals("FAILED")).count();
    }
}
