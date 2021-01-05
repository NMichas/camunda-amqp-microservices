package poc.services.workflow.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poc.common.dto.AMQPMessage;
import poc.common.dto.CommonConfig;

import java.util.logging.Level;

@Log
@Component
public class AMQPServiceReplyListener {

  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  private ProcessEngine camunda;

  private final static ObjectMapper objectMapper = new ObjectMapper();

  @RabbitListener(bindings = @QueueBinding(
      value = @Queue(value = "service_reply"),
      exchange = @Exchange(value = CommonConfig.AMQP_DEFAULT_EXCHANGE),
      key = CommonConfig.AMQP_SERVICE_REPLY_ROUTING_KEY))
  public void execute(String payload) throws JsonProcessingException {
    log.log(Level.INFO, "Received AMQP service_done message: {0}", payload);
    AMQPMessage message = objectMapper.readValue(payload, AMQPMessage.class);

    // The Receive Task might need a few msec to become available, so keep trying in a loop.
    boolean correlationOK = false;
    int tries = 1;
    while (!correlationOK && tries < 51) {
      log.log(Level.INFO, "Triggering workflow service_done for {0}", message.getBusinessKey());
      try {
        camunda.getRuntimeService().createMessageCorrelation("service_done")
            .processInstanceBusinessKey(message.getBusinessKey())
            .setVariable("payload", message.getPayload())
            .correlateWithResult();
        correlationOK = true;
      } catch (MismatchingMessageCorrelationException ex) {
        log.log(Level.INFO, "Receive Task not yet available. Try # {0}.", tries);
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) { }
      }
      tries++;
    }
  }
}
