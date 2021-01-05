package poc.services.rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "poc")
public class AppProperties {
  private String workflowEngineUrl;
  private String startWorkflowUrl;
  private String helloWorkflowKey;
  private String historyProcessUrl;
  private String historyVariableUrl;
}
