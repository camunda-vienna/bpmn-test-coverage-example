package vienna.camunda.meetup.at.service.impl;

import org.springframework.stereotype.Component;
import vienna.camunda.meetup.at.service.Evaluator;

import java.util.logging.Logger;

@Component("evaluator")
public class EvaluatorImpl implements Evaluator {
  Logger logger = Logger.getLogger(EvaluatorImpl.class.getName());

  @Override public boolean evaluateRisk() {
    logger.info("This is a LOG!");
    return true;
  }
}
