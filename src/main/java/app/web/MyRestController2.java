package app.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class MyRestController2 {

    @GetMapping("")
    public String getInfo2(HttpServletRequest request) {

        System.out.println();

        return "Info2";
    }
}
