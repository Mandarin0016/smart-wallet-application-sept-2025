package app.exception;

public class NotificationRetryFailedException extends RuntimeException {

    public NotificationRetryFailedException() {
    }

    public NotificationRetryFailedException(String message) {
        super(message);
    }
}
