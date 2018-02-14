## Standalone Main

### Build

`mvn clean compile assembly:single`

### Run

`java -jar standalone-main-2.20.2-jar-with-dependencies.jar`

`camel.watch.directory` JVM property sets the directory where the routes can be modified or added

Routes are loaded initially from `standalone-main-2.20.2-jar-with-dependencies.jar\META-INF\spring\camel-context.xml` 

### How To
`temp` directory contains files with terminals and transactions. `csv` route expects two files - first the csv file with terminals and next
csv file with transactions (the order is importtant). Processing starts just after receiving the second file with transaction. 

Terminals with transaction gaps are filtered and passed to JIRA processor to create incident with list of terminals 

#### Output

   * Start by `java -jar standalone-main-2.20.2-jar-with-dependencies.jar`
   
```
$ java -jar standalone-main-2.20.2-jar-with-dependencies.jar
Camel context : META-INF/spring/camel-context.xml
Camel watch directory : src/main/resources/META-INF/spring
Feb 14, 2018 3:53:38 PM org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@901c947: startup date [Wed Feb 14 15:53:38 CET 2018]; root of context hierarchy
Feb 14, 2018 3:53:38 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/camel-context.xml]
Feb 14, 2018 3:53:38 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/bar/barContext.xml]
[.reload.CamelReloadMain.main()] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) is starting
[.reload.CamelReloadMain.main()] ManagedManagementStrategy      INFO  JMX is enabled
[.reload.CamelReloadMain.main()] DefaultTypeConverter           INFO  Type converters loaded (core: 192, classpath: 10)
[.reload.CamelReloadMain.main()] SpringCamelContext             INFO  StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
[.reload.CamelReloadMain.main()] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[.reload.CamelReloadMain.main()] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[.reload.CamelReloadMain.main()] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[.reload.CamelReloadMain.main()] SpringCamelContext             INFO  Route: bar started and consuming from: direct://bar
[.reload.CamelReloadMain.main()] SpringCamelContext             INFO  Route: jira started and consuming from: file://jira
[.reload.CamelReloadMain.main()] SpringCamelContext             INFO  Route: csv started and consuming from: file://data?noop=true
[.reload.CamelReloadMain.main()] SpringCamelContext             INFO  Total 3 routes, of which 3 are started
[.reload.CamelReloadMain.main()] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) started in 0.449 seconds
Feb 14, 2018 3:53:40 PM org.springframework.context.support.DefaultLifecycleProcessor start
INFO: Starting beans in phase 2147483646
[.reload.CamelReloadMain.main()] FileWatcherReloadStrategy      INFO  Starting ReloadStrategy to watch directory: src\main\resources\META-INF\spring
[l_dsl) thread #3 - file://data] CsvProcessor                   INFO  Received terminals message with size 1176
[l_dsl) thread #3 - file://data] CsvProcessor                   INFO  Received transactions message with size 10258505
[l_dsl) thread #3 - file://data] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[l_dsl) thread #3 - file://data] CsvProcessor                   INFO  Terminals count = 10
[l_dsl) thread #3 - file://data] TrendAnomaly                   INFO  Max Gap in Seconds 3600, Date 2018-02-13, From 08:00, To 16:00
[l_dsl) thread #3 - file://data] JiraProcessor                  INFO  Received in message : [CH01T02, CH01T03]
[l_dsl) thread #3 - file://data] JiraProcessor                  INFO  JIRA Task JSON {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaciton gap : [CH01T02, CH01T03]","issuetype": {"name": "Task"}}}
[l_dsl) thread #3 - file://data] csv                            INFO  {"id":"169750","key":"NITRADEMO-22","self":"https://aevi-tools.atlassian.net/rest/api/2/issue/169750"}
```

### Changing Route
  
   * Create directory `routes` and copy to it content of `src/main/resources/META-INF/spring
   * Start Camel as `java -Dcamel.watch.directory=routes -jar standalone-main-2.20.2-jar-with-dependencies.jar`
   * Delete `<process ref="jiraProcessor"/>` form `routes/camel-context.xml - JIRA incident won't be created
   * `JiraProcessor` log messages are missing   

   
   