package poc.services.rest.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import poc.services.rest.config.AppProperties;
import poc.services.rest.dto.InstantiateWorkflowDTO;
import poc.services.rest.dto.InstantiateWorkflowDTO.Payload;
import poc.services.rest.dto.InstantiateWorkflowDTO.Variables;

import java.text.MessageFormat;
import java.util.logging.Level;

@Log
@Service
public class WorkflowService {

  private final AppProperties appProperties;
  private WebClient webClient;
  private static ObjectMapper objectMapper = new ObjectMapper();

  public WorkflowService(AppProperties appProperties) {
    this.appProperties = appProperties;
    webClient = WebClient.create(appProperties.getWorkflowEngineUrl());
  }

  public void handleHelloRequest(String requestId, String name) throws JsonProcessingException {
    log.log(Level.INFO, "Starting a new workflow instance with id {0}.", requestId);
    InstantiateWorkflowDTO poCMessage = InstantiateWorkflowDTO.builder()
        .businessKey(requestId)
        .variables(Variables.builder().payload(Payload.builder().value(name).build()).build())
        .build();

    final ClientResponse response = webClient.post()
        .uri(MessageFormat
            .format(appProperties.getStartWorkflowUrl(), appProperties.getHelloWorkflowKey()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new ObjectMapper().writeValueAsString(poCMessage))
        .exchange().block();
    final String reply = response.bodyToMono(String.class).block();
    log.log(Level.INFO, "Workflow engine reply: {0}.", reply);
  }

  public String queryPayload(String businessProcessId) throws JsonProcessingException {
    log.log(Level.INFO, "Querying for process {0}.", businessProcessId);
    // Find the process instance Id from the business id.
    ClientResponse response = webClient.get()
        .uri(MessageFormat.format(appProperties.getHistoryProcessUrl(), businessProcessId))
        .exchange().block();
    String instanceId = objectMapper.readTree(response.bodyToMono(String.class).block())
        .get(0)
        .get("id").asText();
    log.log(Level.INFO, "Business process Id {0} corresponds to process Id {1}",
        new Object[]{businessProcessId, instanceId});

    response = webClient.get()
        .uri(MessageFormat.format(appProperties.getHistoryVariableUrl(), instanceId))
        .exchange().block();
    String variableValue = objectMapper.readTree(response.bodyToMono(String.class).block())
        .get(0)
        .get("value").asText();

    return variableValue;
  }

}
