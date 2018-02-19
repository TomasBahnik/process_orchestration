## Standalone Nitra Camel Demo

### How To
Git `temp` directory contains files with data (`terminals.csv` and `transactions.csv`. `resources/META-INF/spring/csvRoute.xml` 
route expects these files in `data` directory. First the terminals and then transactions (the order is important).
Processing of these two files starts just after receiving the file with transaction. Copy these two file into the 
`data` directory. If you want to init new execution of the `csvRoute.xml` copy the files with another filename 
(could be the same ones) do the `data` directory. Reload of the route initializes new processing.
Directory names below are relative to the working directory = directory where the jar is started. 

   * `resources/nitra-camel.properties` contains configuration for both routes and beans (incl. credentials)
   * route watch directory : `camel.watch.directory` JVM property sets the directory where the routes can be modified or added. 
   When route definition in this directory is changed the route is reloaded. 
      * run with default route watch directory (`routes`)  : `java -jar nitra-camel-2.20.2-standalone-with-dependencies.jar`
      * run with non default route watch directory : `java -Dcamel.watch.directory=your-routes-path -jar nitra-camel-2.20.2-standalone-with-dependencies.jar`
   * Routes are initially loaded from `standalone-main-2.20.2-jar-with-dependencies.jar\META-INF\spring`. 
    
`resources/META-INF/spring/csvRoute.xml` route filters terminals with transaction gap according to `maxGapInSeconds` and `transactionDate` parameters. These parameters
are set in `resources/nitra-camel.properties` or as exchange properties in the `csv` route `resources/META-INF/spring/csvRoute.xml`. 
 List of terminals with transaction gap is passed to JIRA processor to create an incident
 
`resources/META-INF/spring/jiraRoute.xml` sends content of `jira` directory to JIRA

`resources/META-INF/spring/jasperRoute.xml` waits for comma separated `iccIds` in `jasper` directory. Then it calls `getTerminalDetails` method
on Jasper endpoint for each `iccId`. 
 
#### Sample Run

   * Start by `java -jar nitra-camel-2.20.2-standalone-with-dependencies.jar` without any data
   
```
$ java -jar nitra-camel-2.20.2-standalone-with-dependencies.jar
Camel context : META-INF/spring/camel-context.xml
Camel watch directory : routes
Feb 19, 2018 4:35:01 PM org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@4d1b0d2a: startup date [Mon Feb 19 16:35:01 CET 2018]; root of context hierarchy
Feb 19, 2018 4:35:01 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/camel-context.xml]
Feb 19, 2018 4:35:01 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/jasperRoute.xml]
Feb 19, 2018 4:35:02 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/jiraRoute.xml]
Feb 19, 2018 4:35:02 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/csvRoute.xml]
[                          main] JasperProcessor                INFO  Jasper url : jar:file:/C:/Users/moro/git/TomasBahnik/process_orchestration/standalone-main/nitra-camel-2.20.2-standalone-with-dependencies.jar!/org/apache/camel/example/reload/Terminal.wsdl jasper address Terminal.wsdl
Feb 19, 2018 4:35:02 PM org.apache.cxf.wsdl.service.factory.ReflectionServiceFactoryBean buildServiceFromWSDL
INFO: Creating Service {http://api.jasperwireless.com/ws/schema}TerminalService from WSDL: jar:file:/C:/Users/moro/git/TomasBahnik/process_orchestration/standalone-main/nitra-camel-2.20.2-standalone-with-dependencies.jar!/org/apache/camel/example/reload/Terminal.wsdl
Feb 19, 2018 4:35:04 PM org.apache.cxf.wsdl.service.factory.ReflectionServiceFactoryBean buildServiceFromWSDL
INFO: Creating Service {http://api.jasperwireless.com/ws/schema}TerminalService from WSDL: jar:file:/C:/Users/moro/git/TomasBahnik/process_orchestration/standalone-main/nitra-camel-2.20.2-standalone-with-dependencies.jar!/org/apache/camel/example/reload/Terminal.wsdl
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) is starting
[                          main] ManagedManagementStrategy      INFO  JMX is enabled
[                          main] DefaultTypeConverter           INFO  Type converters loaded (core: 192, classpath: 0)
[                          main] HttpComponent                  INFO  Created ClientConnectionManager org.apache.http.impl.conn.PoolingHttpClientConnectionManager@6e23ba17
[                          main] SpringCamelContext             INFO  StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
[                          main] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[                          main] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[                          main] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[                          main] SpringCamelContext             INFO  Route: csv started and consuming from: file://data?noop=true
[                          main] SpringCamelContext             INFO  Route: jira started and consuming from: file://jira
[                          main] SpringCamelContext             INFO  Route: jasper started and consuming from: file://jasper
[                          main] SpringCamelContext             INFO  Total 3 routes, of which 3 are started
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) started in 0.689 seconds
Feb 19, 2018 4:35:06 PM org.springframework.context.support.DefaultLifecycleProcessor start
INFO: Starting beans in phase 2147483646
[                          main] FileWatcherReloadStrategy      INFO  Starting ReloadStrategy to watch directory: routes
```

   * terminals and transactions copied to data directory

```
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  maxGapInSeconds = 2400, transactionDate = 2018-02-13
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  Received terminals message with size 1176
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  Received transactions message with size 10258505
[l_dsl) thread #2 - file://data] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  Terminals count = 10
[l_dsl) thread #2 - file://data] TrendAnomaly                   INFO  Max Gap in Seconds 2400, Date 2018-02-13, From 08:00, To 16:00
[l_dsl) thread #2 - file://data] JiraProcessor                  INFO  maxGapInSeconds = 2400, transactionDate = 2018-02-13
[l_dsl) thread #2 - file://data] JiraProcessor                  INFO  Terminals exceeding transaction gap : [SCS01T01, SCS00T01, SCS00T02, FM02T02, FM01T01, CH00T01, CH01T02, CH01T03, PB01T01, PB01T02]
[l_dsl) thread #2 - file://data] JiraProcessor                  INFO  Payload sent to JIRA {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaction gap(max gap : 2400[sec], Transaction Date : 2018-02-13) [SCS01T01, SCS00T01, SCS00T02, FM02T02, FM01T01, CH00T01, CH01T02, CH01T03, PB01T01, PB01T02]","issuetype": {"name": "Task"}}}
[l_dsl) thread #2 - file://data] csv                            INFO  JIRA response : {"id":"170227","key":"NITRADEMO-54","self":"https://aevi-tools.atlassian.net/rest/api/2/issue/170227"}
```

   * changed `propertyName="csvProcessor.maxGapInSeconds"` in `routes/csvRoute.xml` to `2400`
   
```
[#5 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      WARN  Cannot load the resource C:\Users\moro\git\TomasBahnik\process_orchestration\standalone-main\routes\csvRoute.xml as XML
[#5 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Starting to graceful shutdown 1 routes (timeout 300 seconds)
[_dsl) thread #6 - ShutdownTask] DefaultShutdownStrategy        INFO  Route: csv shutdown complete, was consuming from: file://data?noop=true
[#5 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Graceful shutdown of 1 routes completed in 0 seconds
[#5 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv is stopped, was consuming from: file://data?noop=true
[#5 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv is shutdown and removed, was consuming from: file://data?noop=true
[#5 - FileWatcherReloadStrategy] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[#5 - FileWatcherReloadStrategy] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[#5 - FileWatcherReloadStrategy] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[#5 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv started and consuming from: file://data?noop=true
[#5 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      INFO  Reloaded routes: [csv] from XML resource: C:\Users\moro\git\TomasBahnik\process_orchestration\standalone-main\routes\csvRoute.xml
[l_dsl) thread #7 - file://data] CsvProcessor                   INFO  maxGapInSeconds = 2400, transactionDate = 2018-02-13
[l_dsl) thread #7 - file://data] CsvProcessor                   INFO  Received terminals message with size 1176
[l_dsl) thread #7 - file://data] CsvProcessor                   INFO  Received transactions message with size 10258505
[l_dsl) thread #7 - file://data] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[l_dsl) thread #7 - file://data] CsvProcessor                   INFO  Terminals count = 10
[l_dsl) thread #7 - file://data] TrendAnomaly                   INFO  Max Gap in Seconds 2400, Date 2018-02-13, From 08:00, To 16:00
[l_dsl) thread #7 - file://data] JiraProcessor                  INFO  maxGapInSeconds = 2400, transactionDate = 2018-02-13
[l_dsl) thread #7 - file://data] JiraProcessor                  INFO  Terminals exceeding transaction gap : [SCS01T01, SCS00T01, SCS00T02, FM02T02, FM01T01, CH00T01, CH01T02, CH01T03, PB01T01, PB01T02]
[l_dsl) thread #7 - file://data] JiraProcessor                  INFO  Payload sent to JIRA {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaction gap(max gap : 2400[sec], Transaction Date : 2018-02-13) [SCS01T01, SCS00T01, SCS00T02, FM02T02, FM01T01, CH00T01, CH01T02, CH01T03, PB01T01, PB01T02]","issuetype": {"name": "Task"}}}
[l_dsl) thread #7 - file://data] csv                            INFO  JIRA response : {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaction gap(max gap : 2400[sec], Transaction Date : 2018-02-13) [SCS01T01, SCS00T01, SCS00T02, FM02T02, FM01T01, CH00T01, CH01T02, CH01T03, PB01T01, PB01T02]","issuetype": {"name": "Task"}}}
``` 

### Build

`mvn clean compile package`
