package app.init;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// url - основен път ди микросървиса
@FeignClient(name = "test-svc", url = "http://localhost:8086/api/v1")
public interface TestClient {

    // GET http://localhost:8081/api/v1/notifications/say-hello
    @GetMapping("/notifications/say-hello")
    ResponseEntity<String> getHelloMessage(@RequestParam("name") String name);

}
