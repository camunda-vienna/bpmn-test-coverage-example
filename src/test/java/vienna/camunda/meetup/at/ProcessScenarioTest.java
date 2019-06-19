package vienna.camunda.meetup.at;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.run.ProcessRunner.ExecutableRunner;

import org.mockito.Mock;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import vienna.camunda.meetup.at.service.Evaluator;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

@Deployment(resources = "process.bpmn")
public class ProcessScenarioTest {

  private static final String PROCESS_DEFINITION_KEY = "bpmn-test-coverage-example";

  private static final String CHECK_NEW_ORDER = "CheckNewOrder";

  private static final String INCREASE_ORDER_SPEED = "IncreaseOrderSpeed";

  private static final String ACCEPT_END_EVENT = "AcceptEndEvent";

  private static final String DECLINE_END_EVENT = "DeclineEndEvent";

  private static final String INCREASED_ORDER_SPEED_END_EVENT = "IncreasedOrderSpeedEndEvent";

  @Rule
  @ClassRule
  public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  Map<String, Object> vars = new HashMap<>();

  @Mock
  private ProcessScenario myProcess;

  @Mock
  private Evaluator evaluator;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    Mocks.register("evaluator", evaluator);
  }

  @Test
  public void givenDefaultBehaviour_whenProcessIsRun_verifyHappyPath() {
    //GIVEN
    ExecutableRunner.StartingByKey scenario = Scenario.run(myProcess).startByKey(PROCESS_DEFINITION_KEY);
    when(evaluator.evaluateRisk()).thenReturn(false);

    //WHEN
    scenario.execute();

    //THEN
    verify(myProcess, Mockito.times(1)).hasFinished(ACCEPT_END_EVENT);
  }

  @Test
  public void givenRisk_whenProcessIsRun_verifyOrdercheckAccepted() {
    //GIVEN
    vars.put("accepted", false);

    ExecutableRunner.StartingByKey scenario = Scenario.run(myProcess).startByKey(PROCESS_DEFINITION_KEY);
    when(evaluator.evaluateRisk()).thenReturn(true);
    when(myProcess.waitsAtUserTask(CHECK_NEW_ORDER)).thenReturn(task -> task.complete(vars));

    //WHEN
    scenario.execute();

    //THEN
    verify(myProcess, Mockito.times(1)).hasFinished(ACCEPT_END_EVENT);
  }

  @Test
  public void givenRisk_whenProcessIsRun_verifyOrdercheckNotAccepted() {
    //GIVEN
    vars.put("accepted", true);

    ExecutableRunner.StartingByKey scenario = Scenario.run(myProcess).startByKey(PROCESS_DEFINITION_KEY);
    when(evaluator.evaluateRisk()).thenReturn(true);
    when(myProcess.waitsAtUserTask(CHECK_NEW_ORDER)).thenReturn(task -> task.complete(vars));

    //WHEN
    scenario.execute();

    //THEN
    verify(myProcess, Mockito.times(1)).hasFinished(DECLINE_END_EVENT);
  }

  @Test
  public void givenTimeout_whenProcessIsRun_verifyTimerWorks() {
    //GIVEN
    vars.put("accepted", true);

    ExecutableRunner.StartingByKey scenario = Scenario.run(myProcess).startByKey(PROCESS_DEFINITION_KEY);
    when(evaluator.evaluateRisk()).thenReturn(true);
    when(myProcess.waitsAtUserTask(CHECK_NEW_ORDER)).thenReturn(task -> task.defer("P4D", () -> {}));
    when(myProcess.waitsAtUserTask(INCREASE_ORDER_SPEED)).thenReturn(TaskDelegate::complete);

    //WHEN
    scenario.execute();

    //THEN
    verify(myProcess, Mockito.times(1)).hasFinished(INCREASED_ORDER_SPEED_END_EVENT);
  }

}
