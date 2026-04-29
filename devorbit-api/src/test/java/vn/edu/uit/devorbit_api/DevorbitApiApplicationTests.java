package vn.edu.uit.devorbit_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "app.jwt.secret=test-jwt-secret",
    "app.github.token=test-github-token"
})
class DevorbitApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
