# <img src="/doc/img/camunda.png" width="23" height="23" />&nbsp;Camunda&nbsp;BPM&nbsp;Process&nbsp;Test&nbsp;Coverage&nbsp;Example

This project is an example on how to use process test coverage with camunda. This example is build with the archetype of camunda-spring-boot.
For more information of how to use camunda archetypes, please check following [link](https://github.com/camunda/camunda-archetypes) 

The maven archetype used is the **org.camunda.bpm.archetype:camunda-archetype-spring-boot**.

## Highlights

* Example uses Camunda BPM 7.11.0 
* Archetype includes dependencies for **camunda-bpm-assert-scenario & camunda-bpm-process-test-coverage**
* Showcases basic ordering process with timer

## How to start

**1.** Create new maven project from archetype **org.camunda.bpm.archetype:camunda-archetype-spring-boot**

**2.** Run maven clean install

**3.** Uncomment **camunda-bpm-process-test-coverage dependency** in the pom.xml

**4.** Create a test resource named camunda.cfg.xml in the src/test/resources folder

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    
    <bean id="processEngineConfiguration" class="org.camunda.bpm.extension.process_test_coverage.junit.rules.ProcessCoverageInMemProcessEngineConfiguration"/>

</beans>
```
**5.** Add the deployed bpmn to the test class by annotating it with:
```java
@Deployment(resources = "'YOUR_PROCESS_NAME'.bpmn")
public class ProcessScenarioTest {
  ...
}
```

**6.** Use the **TestCoverageProcessEngineRule** as your process engine JUnit rule
       
```java
@Rule
@ClassRule
public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();
```

**7.** Create a business process (.bpmn) that you want to test.
![Demo Application](/doc/img/orderProcessCamundaMeetup.png)

**8.** Write some test scenarios for your business process. How to use process scenarios is shown here: [link](https://github.com/camunda/camunda-bpm-assert-scenario) 

**9.** Run **mvn clean install** to build the project and run all the tests

**10.** Go to target/process-test-coverage/**the_name_of_your_test**. There you will find html files for each of your test cases, showing the coverage of the bpmn. E.g:
![Coverage](/doc/img/CoverageExample.png)

**11.** To execute the spring-boot-application create a jar with following command: **mvn package** 

**12.** Then run the application with java -jar target/**the_name_of_the_project**.jar

**13.** On how to start a process and how to check it in the camunda cockpit, please read the official camunda documentation: [link](https://docs.camunda.org/manual/7.11/user-guide/)

## Note
### Camunda Version in the archetype.

As of now (Early June 2019), the camunda archetype does include the camunda version 7.9.0.
To use the new camunda version 7.11.0 please change following versions in pom.xml

```xml
<properties>
    <camunda.version>7.11.0</camunda.version>
    <camundaSpringBoot.version>3.3.1</camundaSpringBoot.version>
    <springBoot.version>2.1.5.RELEASE</springBoot.version>
</properties>
```
