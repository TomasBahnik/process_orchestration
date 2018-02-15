## Standalone Nitra Camel Demo

### How To

   * `nitra-camel.properties` contains configuration for both routes and beans
   * run : `java -jar standalone-main-2.20.2-jar-with-dependencies.jar`
   * config route watch directory : `camel.watch.directory` JVM property sets the directory where the routes can be modified or added
   * `terminals.csv` and `transactions.csv` files are loaded from `data` directory
   
Directories are relative to working directory = where the jar is started 

Routes are loaded initially from `standalone-main-2.20.2-jar-with-dependencies.jar\META-INF\spring`. There is directory `bar` and file 
`camel-context.xml` which imports `bar/barContext.xml`. This illustrates that routes can be kept in separate files  
 
Git `temp` directory contains files with data (`terminals.csv` and `transactions.csv`. `csv` route expects these files id `data` directory
- first the terminals and then transactions (the order is important). Processing of these two starts just after receiving
the second file with transaction. Copy these two file in `data` directory. If you want to init new execution of the process copy files with
another name (could be the same ones) do `data` directory

Terminals with transaction gaps are filtered and passed to JIRA processor to create incident with list of terminals

This process is defined in `camel-context.xml` in the `<route id="csv">` 

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

   * `camel.watch.directory` JVM property sets the directory where the routes can be modified or added (passed to JDV as e.g. 
   `java -Dcamel.watch.directory=routes`)
   * Create directory `routes` and copy to it content of `standalone-main-2.20.2-jar-with-dependencies.jar\META-INF\spring`
   * Clean `data` directory (delete `terminal.csv` and `transactions.csv`)
   * Start Camel as `java -Dcamel.watch.directory=routes -jar standalone-main-2.20.2-jar-with-dependencies.jar`
   * Delete `<to uri="https://aevi-tools.atlassian.net/rest/api/2/issue"/>` from `routes/camel-context.xml` - JIRA incident won't be created
   
When started Camel context and watch directories are logged (see output below))
 
```
$ java -Dcamel.watch.directory=routes -jar standalone-main-2.20.2-jar-with-dependencies.jar
Camel context : META-INF/spring/camel-context.xml
Camel watch directory : routes
Feb 15, 2018 8:36:52 AM org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@4d1b0d2a: startup date [Thu Feb 15 08:36:52 CET 2018]; root of context hierarchy
Feb 15, 2018 8:36:52 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/camel-context.xml]
Feb 15, 2018 8:36:53 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/bar/barContext.xml]
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) is starting
[                          main] ManagedManagementStrategy      INFO  JMX is enabled
[                          main] DefaultTypeConverter           INFO  Type converters loaded (core: 192, classpath: 0)
[                          main] SpringCamelContext             INFO  StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
[                          main] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[                          main] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[                          main] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[                          main] SpringCamelContext             INFO  Route: bar started and consuming from: direct://bar
[                          main] SpringCamelContext             INFO  Route: jira started and consuming from: file://jira
[                          main] SpringCamelContext             INFO  Route: csv started and consuming from: file://data?noop=true
[                          main] SpringCamelContext             INFO  Total 3 routes, of which 3 are started
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) started in 0.464 seconds
Feb 15, 2018 8:36:54 AM org.springframework.context.support.DefaultLifecycleProcessor start
INFO: Starting beans in phase 2147483646
[                          main] FileWatcherReloadStrategy      INFO  Starting ReloadStrategy to watch directory: routes
```

When csv files are copied to `data` directory, processing starts

```
[                          main] SpringCamelContext             INFO  Total 3 routes, of which 3 are started
[l_dsl) thread #3 - file://data] CsvProcessor                   INFO  Received terminals message with size 1176xml_dsl) started in 0.464 seconds
[l_dsl) thread #3 - file://data] CsvProcessor                   INFO  Received transactions message with size 10258505
[l_dsl) thread #3 - file://data] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[l_dsl) thread #3 - file://data] CsvProcessor                   INFO  Terminals count = 10egy to watch directory: routes
[l_dsl) thread #3 - file://data] TrendAnomaly                   INFO  Max Gap in Seconds 3600, Date 2018-02-13, From 08:00, To 16:00
[l_dsl) thread #3 - file://data] JiraProcessor                  INFO  Received in message : [CH01T02, CH01T03]
[l_dsl) thread #3 - file://data] JiraProcessor                  INFO  JIRA Task JSON {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaciton gap : [CH01T02, CH01T03]","issuetype": {"name": "Task"}}}
[l_dsl) thread #3 - file://data] csv                            INFO  {"id":"169817","key":"NITRADEMO-39","self":"https://aevi-tools.atlassian.net/rest/api/2/issue/169817"}
```

at the end JIRA ticket `"key":"NITRADEMO-39"` is created

When `<to uri="https://aevi-tools.atlassian.net/rest/api/2/issue"/>` is deleted in `routes/camel-context.xml` and file is saved routes are reloaded
and run again, now without sending ticket to JIRA

```
[#4 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      WARN  Cannot load the resource C:\Users\moro\temp\camel\routes\camel-context.xml as XML
[#4 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Starting to graceful shutdown 1 routes (timeout 300 seconds)
[_dsl) thread #5 - ShutdownTask] DefaultShutdownStrategy        INFO  Route: jira shutdown complete, was consuming from: file://jira
[#4 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Graceful shutdown of 1 routes completed in 0 seconds
[#4 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: jira is stopped, was consuming from: file://jira
[#4 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: jira is shutdown and removed, was consuming from: file://jira
[#4 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Starting to graceful shutdown 1 routes (timeout 300 seconds)
[_dsl) thread #5 - ShutdownTask] DefaultShutdownStrategy        INFO  Route: csv shutdown complete, was consuming from: file://data?noop=true
[#4 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Graceful shutdown of 1 routes completed in 0 seconds
[#4 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv is stopped, was consuming from: file://data?noop=true
[#4 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv is shutdown and removed, was consuming from: file://data?noop=true
[#4 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: jira started and consuming from: file://jira
[#4 - FileWatcherReloadStrategy] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[#4 - FileWatcherReloadStrategy] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[#4 - FileWatcherReloadStrategy] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[#4 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv started and consuming from: file://data?noop=true
[#4 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      INFO  Reloaded routes: [jira,csv] from XML resource: C:\Users\moro\temp\camel\routes\camel-context.xml
[l_dsl) thread #7 - file://data] CsvProcessor                   INFO  Received terminals message with size 1176
[l_dsl) thread #7 - file://data] CsvProcessor                   INFO  Received transactions message with size 10258505
[l_dsl) thread #7 - file://data] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[l_dsl) thread #7 - file://data] CsvProcessor                   INFO  Terminals count = 10
[l_dsl) thread #7 - file://data] TrendAnomaly                   INFO  Max Gap in Seconds 3600, Date 2018-02-13, From 08:00, To 16:00
[l_dsl) thread #7 - file://data] JiraProcessor                  INFO  Received in message : [CH01T02, CH01T03]
[l_dsl) thread #7 - file://data] JiraProcessor                  INFO  JIRA Task JSON {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaciton gap : [CH01T02, CH01T03]","issuetype": {"name": "Task"}}}
[l_dsl) thread #7 - file://data] csv                            INFO  {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaciton gap : [CH01T02, CH01T03]","issuetype": {"name": "Task"}}}
``` 
  
### Build

`mvn clean compile assembly:single`
