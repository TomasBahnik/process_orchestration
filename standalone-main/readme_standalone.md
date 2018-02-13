## Standalone Main

### Build

` mvn clean compile assembly:single`

### Run

`java -jar standalone-main-2.20.2-jar-with-dependencies.jar`

`camel.watch.directory` property sets the directory where the routes can be modified or added

`temp` directory contains files with transactions. When one of these files is copied to `data` directory it is processed by `csv` route.

Routes are loaded initially from `standalone-main-2.20.2-jar-with-dependencies.jar\META-INF\spring\camel-context.xml` 


### Expected Outpup

```
$ java -jar target/standalone-main-2.20.2-jar-with-dependencies.jar
Camel context : META-INF/spring/camel-context.xml
Camel watch directory : src/main/resources/META-INF/spring
Feb 13, 2018 12:21:50 PM org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@4fcd19b3: startup date [Tue Feb 13 12:21:50 CET 2018]; root of context hierarchy
Feb 13, 2018 12:21:50 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/camel-context.xml]
Feb 13, 2018 12:21:50 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/bar/barContext.xml]
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) is starting
[                          main] ManagedManagementStrategy      INFO  JMX is enabled
[                          main] DefaultTypeConverter           INFO  Type converters loaded (core: 192, classpath: 0)
[                          main] SpringCamelContext             INFO  StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
[                          main] FileEndpoint                   INFO  Endpoint is configured with noop=true so forcing endpoint to be idempotent as well
[                          main] FileEndpoint                   INFO  Using default memory based idempotent repository with cache max size: 1000
[                          main] AggregateProcessor             INFO  Defaulting to MemoryAggregationRepository
[                          main] SpringCamelContext             INFO  Route: bar started and consuming from: direct://bar
[                          main] SpringCamelContext             INFO  Route: csv started and consuming from: file://data?noop=true
[                          main] SpringCamelContext             INFO  Total 2 routes, of which 2 are started
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) started in 0.302 seconds
Feb 13, 2018 12:21:52 PM org.springframework.context.support.DefaultLifecycleProcessor start
INFO: Starting beans in phase 2147483646
[                          main] FileWatcherReloadStrategy      INFO  Starting ReloadStrategy to watch directory: src\main\resources\META-INF\spring
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  Received terminals message with size 73448
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  Received transactions message with size 49722121
[l_dsl) thread #2 - file://data] ExpectedTerminalDataUsage      INFO  Terminals count = 567
[l_dsl) thread #2 - file://data] ExpectedTerminalDataUsage      INFO  Transactions count = 98334
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  TerminalID WINC34: Usage 20000 MB
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  TerminalID WINC31: Usage 4000 MB
[l_dsl) thread #2 - file://data] AccessoryOrderProcessor        INFO  Received in message with 10 trx, paper per trx 14
[l_dsl) thread #2 - file://data] AccessoryOrderProcessor        INFO  Paper consumed 140
```
