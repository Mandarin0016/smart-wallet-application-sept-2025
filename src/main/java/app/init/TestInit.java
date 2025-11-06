package app.init;

import feign.FeignException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestInit implements ApplicationRunner {

    private final TestClient testClient;

    public TestInit(TestClient testClient) {
        this.testClient = testClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        try {
            ResponseEntity<String> response = testClient.getHelloMessage("Ivan");
        } catch (FeignException e) {
            System.out.println();
        }

//        System.out.println(response.getBody());

    }
}
