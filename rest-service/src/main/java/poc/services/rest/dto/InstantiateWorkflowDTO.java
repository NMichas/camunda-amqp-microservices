package poc.services.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstantiateWorkflowDTO {
  private String businessKey;
  private Variables variables;

  @Data
  @Builder
  public static class Variables {
    private Payload payload;
  }

  @Data
  @Builder
  public static class Payload {
    private final String type = "String";
    private String value;
  }

}
