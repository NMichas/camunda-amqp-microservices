package poc.services.workflow;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class CamundaService {

  public static void main(String... args) {
    SpringApplication.run(CamundaService.class, args);
  }
}
