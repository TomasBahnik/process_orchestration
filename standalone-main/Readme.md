## Standalone Nitra Camel Demo

### How To

Directory names below are relative to the working directory = directory where the jar is started. 

   * Git `temp` directory contains files with data (`terminals.csv` `terminal-data-usage.csv` and `transactions.csv`. 
   * route `resources/META-INF/spring/dataUsageRoute.xml` expects `terminals.csv` `terminal-data-usage.csv` and `transactions.csv` 
   (in this order!)  in `data-usage` directory. Processing starts after 3rd file.
   * `resources/META-INF/spring/maxGapRoute.xml` expects `terminals.csv` and `transactions.csv`  (in this order!)  in `data-maxGap` directory
   Processing starts after 2nd file.  
   * **delete** these files before changing these routes othewise the order can be broken  
   * `resources/nitra-camel.properties` contains configuration for both routes and beans (credentials to JIRA are empty)
   * route watch directory : `camel.watch.directory` JVM property sets the directory where the routes can be modified or added. 
   When route definition in this directory is changed the route is reloaded. 
      * run with default route watch directory (`routes`)  : `java -jar nitra-camel-2.20.2-standalone-with-dependencies.jar`
      * run with non default route watch directory : `java -Dcamel.watch.directory=your-routes-path -jar nitra-camel-2.20.2-standalone-with-dependencies.jar`
   * Routes are initially loaded from `nitra-camel-2.20.2-standalone-with-dependencies.jar\META-INF\spring`. 

#### maxGapRoute

`resources/META-INF/spring/maxGapRoute.xml` route filters terminals with transaction gap according to 
`maxGapProcessor.maxGapInSeconds` and `maxGapProcessor.transactionDate` parameters. These parameters are set in `resources/nitra-camel.properties`
or as exchange properties in the `maxGapRoute.xml` route. List of terminals with transaction gap is passed to JIRA 
processor to create an incident.

#### dataUsageRoute

`resources/META-INF/spring/dataUsageRoute.xml` counts terminal data usage based on `dataUsage.bytesPerTransaction` parameter (`nitra-camel.properties` or 
`dataUsageRoute.xml`) and sends terminals with real data usage higher than expected to JIRA as incident.

#### jasperRoute

`resources/META-INF/spring/jasperRoute.xml` waits for comma separated `iccIds` in `jasper` directory. Then it calls `getTerminalDetails` method
on Jasper endpoint for each `iccId`. 
 
#### Sample Run

   * Start by `java -jar nitra-camel-2.20.2-standalone-with-dependencies.jar` without any data
   
```
$ java -jar nitra-camel-2.20.2-standalone-with-dependencies.jar
Camel context : META-INF/spring/camel-context.xml
Camel watch directory : routes
Feb 20, 2018 11:32:23 AM org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@4d1b0d2a: startup date [Tue Feb 20 11:32:23 CET 2018]; root of context hierarchy
Feb 20, 2018 11:32:23 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/camel-context.xml]
Feb 20, 2018 11:32:23 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/jasperRoute.xml]
Feb 20, 2018 11:32:24 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/jiraRoute.xml]
Feb 20, 2018 11:32:24 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/maxGapRoute.xml]
Feb 20, 2018 11:32:24 AM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/dataUsageRoute.xml]
[                          main] JasperProcessor                INFO  Jasper url : jar:file:/C:/Users/moro/git/TomasBahnik/process_orchestration/standalone-main/target/nitra-camel-2.20.2-standalone-with-dependencies.jar!/org/apache/camel/example/reload/Terminal.wsdl jasper address Terminal.wsdl
Feb 20, 2018 11:32:25 AM org.apache.cxf.wsdl.service.factory.ReflectionServiceFactoryBean buildServiceFromWSDL
INFO: Creating Service {http://api.jasperwireless.com/ws/schema}TerminalService from WSDL: jar:file:/C:/Users/moro/git/TomasBahnik/process_orchestration/standalone-main/target/nitra-camel-2.20.2-standalone-with-dependencies.jar!/org/apache/camel/example/reload/Terminal.wsdl
Feb 20, 2018 11:32:27 AM org.apache.cxf.wsdl.service.factory.ReflectionServiceFactoryBean buildServiceFromWSDL
INFO: Creating Service {http://api.jasperwireless.com/ws/schema}TerminalService from WSDL: jar:file:/C:/Users/moro/git/TomasBahnik/process_orchestration/standalone-main/target/nitra-camel-2.20.2-standalone-with-dependencies.jar!/org/apache/camel/example/reload/Terminal.wsdl
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) is starting
[                          main] ManagedManagementStrategy      INFO  JMX is enabled
[                          main] DefaultTypeConverter           INFO  Type converters loaded (core: 192, classpath: 0)
[                          main] HttpComponent                  INFO  Created ClientConnectionManager org.apache.http.impl.conn.PoolingHttpClientConnectionManager@17034458
[                          main] SpringCamelContext             INFO  StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
[                          main] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[                          main] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[                          main] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[                          main] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[                          main] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[                          main] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[                          main] SpringCamelContext             INFO  Route: dataUsageRoute started and consuming from: file://data-usage?noop=true
[                          main] SpringCamelContext             INFO  Route: maxGapRoute started and consuming from: file://data-maxGap?noop=true
[                          main] SpringCamelContext             INFO  Route: jira started and consuming from: file://jira
[                          main] SpringCamelContext             INFO  Route: jasper started and consuming from: file://jasper
[                          main] SpringCamelContext             INFO  Total 4 routes, of which 4 are started
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) started in 0.726 seconds
Feb 20, 2018 11:32:28 AM org.springframework.context.support.DefaultLifecycleProcessor start
INFO: Starting beans in phase 2147483646
[                          main] FileWatcherReloadStrategy      INFO  Starting ReloadStrategy to watch directory: routes
```

   * copy `terminal.csv` and `transactions.csv` CSV files to `data-maxGap` directory

```
[thread #3 - file://data-maxGap] MaxGapProcessor                INFO  maxGapInSeconds = 2400, transactionDate = 2018-02-13
[thread #3 - file://data-maxGap] MaxGapProcessor                INFO  Received terminals message with size 1176
[thread #3 - file://data-maxGap] MaxGapProcessor                INFO  Received transactions message with size 10258505
[thread #3 - file://data-maxGap] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[thread #3 - file://data-maxGap] MaxGapProcessor                INFO  Terminals count = 10
[thread #3 - file://data-maxGap] TrendAnomaly                   INFO  Max Gap in Seconds 2400, Date 2018-02-13, From 08:00, To 16:00
[thread #3 - file://data-maxGap] JiraProcessor                  INFO  Max Gap = 2400 sec, Transaction Date = 2018-02-13
[thread #3 - file://data-maxGap] JiraProcessor                  INFO  Terminals exceeding transaction gap : [SCS01T01, SCS00T01, SCS00T02, FM02T02, FM01T01, CH00T01, CH01T02, CH01T03, PB01T01, PB01T02]
[thread #3 - file://data-maxGap] JiraProcessor                  INFO  Payload sent to JIRA {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaction gap(Max Gap : 2400sec, Transaction Date : 2018-02-13) [SCS01T01, SCS00T01, SCS00T02, FM02T02, FM01T01, CH00T01, CH01T02, CH01T03, PB01T01, PB01T02]","issuetype": {"name": "Task"}}}
```

   * delete csv files from `data-maxGap` directory and change `propertyName="maxGapProcessor.maxGapInSeconds"` in `routes/maxGapRoute.xml` to `3600`
   
```
[#6 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      WARN  Cannot load the resource C:\Users\moro\git\TomasBahnik\process_orchestration\standalone-main\routes\maxGapRoute.xml as XML
[#6 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Starting to graceful shutdown 1 routes (timeout 300 seconds)
[_dsl) thread #7 - ShutdownTask] DefaultShutdownStrategy        INFO  Route: maxGapRoute shutdown complete, was consuming from: file://data-maxGap?noop=true
[#6 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Graceful shutdown of 1 routes completed in 0 seconds
[#6 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: maxGapRoute is stopped, was consuming from: file://data-maxGap?noop=true
[#6 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: maxGapRoute is shutdown and removed, was consuming from: file://data-maxGap?noop=true
[#6 - FileWatcherReloadStrategy] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[#6 - FileWatcherReloadStrategy] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[#6 - FileWatcherReloadStrategy] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[#6 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: maxGapRoute started and consuming from: file://data-maxGap?noop=true
[#6 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      INFO  Reloaded routes: [maxGapRoute] from XML resource: C:\Users\moro\git\TomasBahnik\process_orchestration\standalone-main\routes\maxGapRoute.xml
[#6 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Starting to graceful shutdown 1 routes (timeout 300 seconds)
[_dsl) thread #7 - ShutdownTask] DefaultShutdownStrategy        INFO  Route: maxGapRoute shutdown complete, was consuming from: file://data-maxGap?noop=true
[#6 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Graceful shutdown of 1 routes completed in 0 seconds
[#6 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: maxGapRoute is stopped, was consuming from: file://data-maxGap?noop=true
[#6 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: maxGapRoute is shutdown and removed, was consuming from: file://data-maxGap?noop=true
[#6 - FileWatcherReloadStrategy] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[#6 - FileWatcherReloadStrategy] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[#6 - FileWatcherReloadStrategy] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[#6 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: maxGapRoute started and consuming from: file://data-maxGap?noop=true
[#6 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      INFO  Reloaded routes: [maxGapRoute] from XML resource: C:\Users\moro\git\TomasBahnik\process_orchestration\standalone-main\routes\maxGapRoute.xml
```
 
   * copy `terminal.csv` and `transactions.csv` CSV files to `data-maxGap` directory

```
[thread #9 - file://data-maxGap] MaxGapProcessor                INFO  maxGapInSeconds = 3600, transactionDate = 2018-02-13
[thread #9 - file://data-maxGap] MaxGapProcessor                INFO  Received terminals message with size 1176
[thread #9 - file://data-maxGap] MaxGapProcessor                INFO  Received transactions message with size 10258505
[thread #9 - file://data-maxGap] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[thread #9 - file://data-maxGap] MaxGapProcessor                INFO  Terminals count = 10
[thread #9 - file://data-maxGap] TrendAnomaly                   INFO  Max Gap in Seconds 3600, Date 2018-02-13, From 08:00, To 16:00
[thread #9 - file://data-maxGap] JiraProcessor                  INFO  Max Gap = 3600 sec, Transaction Date = 2018-02-13
[thread #9 - file://data-maxGap] JiraProcessor                  INFO  Terminals exceeding transaction gap : [CH01T02, CH01T03]
[thread #9 - file://data-maxGap] JiraProcessor                  INFO  Payload sent to JIRA {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals with transaction gaps","description": "Terminals exceeding transaction gap(Max Gap : 3600sec, Transaction Date : 2018-02-13) [CH01T02, CH01T03]","issuetype": {"name": "Task"}}}
```

   * copy `terminal.csv` `terminal-data-usage.csv` and `transactions.csv` CSV files to `data-usage` directory

```
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  Bytes Per Transaction = 2500
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  Received terminals message with size 1176
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  Received transactions message with size 10258505
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  Received terminal data usage message with size 153
[ thread #2 - file://data-usage] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[ thread #2 - file://data-usage] ExpectedTerminalDataUsage      INFO  Terminals with data usage count = 10
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  Terminals count = 10
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  Terminals with data usage count = 10
[ thread #2 - file://data-usage] ExpectedTerminalDataUsage      INFO  Transactions count = 18780
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID SCS01T01: Expected/Real Usage  50000/50000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID SCS00T01: Expected/Real Usage  55000/45000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID SCS00T02: Expected/Real Usage  55000/44000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID FM02T02: Expected/Real Usage  42500/34000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID FM01T01: Expected/Real Usage  50000/40000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID CH00T01: Expected/Real Usage  92500/74000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID CH01T02: Expected/Real Usage  45000/36000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID CH01T03: Expected/Real Usage  30000/24000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID PB01T01: Expected/Real Usage  87500/70000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  TerminalID PB01T02: Expected/Real Usage  52500/42000 MB
[ thread #2 - file://data-usage] DataUsageProcessor             INFO  No terminals with exceeded usage
[ thread #2 - file://data-usage] JiraProcessor                  INFO  Terminals data usage :
[ thread #2 - file://data-usage] JiraProcessor                  INFO  Payload sent to JIRA {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals data usage exceeded","description": "Terminals data usage : ","issuetype": {"name": "Task"}}}
[ thread #2 - file://data-usage] dataUsageRoute                 INFO  Do not send to JIRA
```

   * delete `terminal.csv` `terminal-data-usage.csv` and `transactions.csv` CSV files to `data-usage` directory
   and change  `propertyName="dataUsage.bytesPerTransaction"` in `routes/dataUsageRoute.xml` to `2000`
   * copy `terminal.csv` `terminal-data-usage.csv` and `transactions.csv` CSV files to `data-usage` directory

```
[thread #12 - file://data-usage] DataUsageProcessor             INFO  Bytes Per Transaction = 2000
[thread #12 - file://data-usage] DataUsageProcessor             INFO  Received terminals message with size 1176
[thread #12 - file://data-usage] DataUsageProcessor             INFO  Received transactions message with size 10258505
[thread #12 - file://data-usage] DataUsageProcessor             INFO  Received terminal data usage message with size 153
[thread #12 - file://data-usage] ExpectedTerminalDataUsage      INFO  Terminals count = 10
[thread #12 - file://data-usage] ExpectedTerminalDataUsage      INFO  Terminals with data usage count = 10
[thread #12 - file://data-usage] DataUsageProcessor             INFO  Terminals count = 10
[thread #12 - file://data-usage] DataUsageProcessor             INFO  Terminals with data usage count = 10
[thread #12 - file://data-usage] ExpectedTerminalDataUsage      INFO  Transactions count = 18780
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID SCS01T01: Expected/Real Usage  40000/50000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID SCS00T01: Expected/Real Usage  44000/45000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID SCS00T02: Expected/Real Usage  44000/44000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID FM02T02: Expected/Real Usage  34000/34000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID FM01T01: Expected/Real Usage  40000/40000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID CH00T01: Expected/Real Usage  74000/74000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID CH01T02: Expected/Real Usage  36000/36000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID CH01T03: Expected/Real Usage  24000/24000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID PB01T01: Expected/Real Usage  70000/70000 MB
[thread #12 - file://data-usage] DataUsageProcessor             INFO  TerminalID PB01T02: Expected/Real Usage  42000/42000 MB
[thread #12 - file://data-usage] JiraProcessor                  INFO  Terminals data usage : SCS01T01:expected/real 40000/50000 MB,SCS00T01:expected/real 44000/45000 MB,
[thread #12 - file://data-usage] JiraProcessor                  INFO  Payload sent to JIRA {"fields":{"project":{"key": "NITRADEMO"},"summary": "Incident-Terminals data usage exceeded","description": "Terminals data usage : SCS01T01:expected/real 40000/50000 MB,SCS00T01:expected/real 44000/45000 MB,","issuetype": {"name": "Task"}}}
```
   
### Build

`mvn clean compile package`
