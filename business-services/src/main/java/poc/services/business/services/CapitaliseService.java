package poc.services.business.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poc.common.dto.AMQPMessage;
import poc.common.dto.CommonConfig;

import java.util.logging.Level;

@Log
@Component
public class CapitaliseService {

  @Autowired
  protected RabbitTemplate rabbitTemplate;

  private final static ObjectMapper objectMapper = new ObjectMapper();

  @RabbitListener(bindings = @QueueBinding(
      value = @Queue(value = "capitalise"),
      exchange = @Exchange(value = CommonConfig.AMQP_DEFAULT_EXCHANGE),
      key = "capitalise"))
  public void execute(String payload) throws JsonProcessingException {
    AMQPMessage message = objectMapper.readValue(payload, AMQPMessage.class);
    log.log(Level.INFO, "Received AMQP message: {0}", message);

    log.log(Level.INFO, "Processing payload for AMQP message: {0}", message);
    message.setPayload(message.getPayload().toUpperCase());

    log.log(Level.INFO, "Publishing AMQP reply: {0}", message);
    rabbitTemplate
        .convertAndSend(CommonConfig.AMQP_DEFAULT_EXCHANGE,
            CommonConfig.AMQP_SERVICE_REPLY_ROUTING_KEY,
            objectMapper.writeValueAsString(message));
  }
}
