package poc.services.rest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import poc.services.rest.services.ValidatorService;
import poc.services.rest.services.WorkflowService;

import java.text.MessageFormat;
import java.util.UUID;

@RestController
public class HelloResource {

  private final ValidatorService validatorService;
  private final WorkflowService workflowService;

  public HelloResource(ValidatorService validatorService,
      WorkflowService workflowService) {
    this.validatorService = validatorService;
    this.workflowService = workflowService;
  }

  @GetMapping(path = "hello", consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity hello(@RequestParam String name) throws JsonProcessingException {
    // Validate the request.
    if (!validatorService.isNameValid(name)) {
      return ResponseEntity.badRequest()
          .body(MessageFormat.format("Provided name ''{0}'' is not valid.", name));
    }

    // Generate a random Id for this request, so that it can be tracked throughout the workflow
    // execution.
    String requestId = UUID.randomUUID().toString();

    // Create a new workflow instance to handle this request.
    workflowService.handleHelloRequest(requestId, name);

    // Return the generated request Id to the client.
    return ResponseEntity.ok(requestId);
  }
}
