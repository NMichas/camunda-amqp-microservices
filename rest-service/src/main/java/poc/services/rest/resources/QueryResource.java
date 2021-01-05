package poc.services.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import poc.services.rest.services.WorkflowService;

@RestController
public class QueryResource {

  private final WorkflowService workflowService;

  public QueryResource(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  @GetMapping(path = "query", consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
  public String hello(@RequestParam String id) throws JsonProcessingException {
    return workflowService.queryPayload(id);
  }
}
