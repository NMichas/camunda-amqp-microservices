package poc.services.workflow.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poc.common.dto.AMQPMessage;
import poc.common.dto.CommonConfig;

import java.util.logging.Level;

/**
 * A helper class to allow Service Tasks to publish to AMQP.
 */
@Component
@Log
public class AMQPPublishDelegate implements JavaDelegate {

  @Autowired
  protected RabbitTemplate rabbitTemplate;

  private final static ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    String payload = delegateExecution.getVariable("payload").toString();
    String routingKey = delegateExecution.getVariable("routingKey").toString();
    String businessKey = delegateExecution.getBusinessKey();
    log.log(Level.INFO, "Received a call from Workflow engine. Routing key = {0}, Payload = {1}.",
        new Object[]{routingKey, payload});

    String message = objectMapper.writeValueAsString(AMQPMessage.builder()
        .businessKey(businessKey)
        .payload(payload)
        .build());

    log.log(Level.INFO, "Publishing AMQP message for service execution: {0}", message);
    rabbitTemplate.convertAndSend(CommonConfig.AMQP_DEFAULT_EXCHANGE, routingKey, message);
  }
}
