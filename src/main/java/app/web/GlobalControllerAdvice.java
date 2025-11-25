package app.web;

import app.exception.NotificationRetryFailedException;
import app.exception.UserNotFoundException;
import app.exception.UsernameAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleException(UserNotFoundException e) {

        ModelAndView modelAndView = new ModelAndView("not-found");

        return modelAndView;
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExistException(UsernameAlreadyExistException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(NotificationRetryFailedException.class)
    public String handleNotificationRetryFailedException(NotificationRetryFailedException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/notifications";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NoResourceFoundException.class,
            AccessDeniedException.class
    })
    public ModelAndView handleSpringException() {

        ModelAndView modelAndView = new ModelAndView("not-found");

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleLeftoverExceptions(Exception e) {

        ModelAndView modelAndView = new ModelAndView("internal-server-error");

        return modelAndView;
    }
}
