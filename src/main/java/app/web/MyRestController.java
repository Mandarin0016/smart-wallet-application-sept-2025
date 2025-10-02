package app.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/demo")
public class MyRestController {

    @GetMapping("/info")
    public String getInfo(HttpServletRequest request, HttpServletResponse response) {

        // Cookies follows format "K=V"
        Cookie cookie = new Cookie("userId", UUID.randomUUID().toString());
        cookie.setMaxAge(25);
        response.addCookie(cookie);

        return "Done";
    }

    @GetMapping("/info2")
    public String getInfo2(HttpServletRequest request) {

        System.out.println();

        return "Info2";
    }
}
