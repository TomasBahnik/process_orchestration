## Standalone Main

### Build

` mvn clean compile assembly:single`

### Run

`java -jar standalone-main-2.20.2-jar-with-dependencies.jar`

`camel.watch.directory` property sets the directory where the routes can be modified or added

`temp` directory contains files with transactions. When one of these files is copied to `data` directory it is processed by `csv` route.

Routes are loaded initially from `standalone-main-2.20.2-jar-with-dependencies.jar\META-INF\spring\camel-context.xml` 

There are two processors in `csv` route

```
        <route id="csv">
            <from uri="file://data"/>
            <setHeader headerName="subject">
                <constant>new incident reported</constant>
            </setHeader>
            <process ref="csvProcessor"/>
            <process ref="accessoryOrderProcessor"/>
        </route>
```

Output below shows how the processing changes when `accessoryOrderProcessor` is removed

```
$ java -Dcamel.watch.directory=routes -jar standalone-main-2.20.2-jar-with-dependencies.jar
Camel context : META-INF/spring/camel-context.xml
Camel watch directory : routes
Feb 12, 2018 12:48:53 PM org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
INFO: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@4fcd19b3: startup date [Mon Feb 12 12:48:53 CET 2018]; root of context hierarchy
Feb 12, 2018 12:48:53 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/camel-context.xml]
Feb 12, 2018 12:48:53 PM org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
INFO: Loading XML bean definitions from class path resource [META-INF/spring/bar/barContext.xml]
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) is starting
[                          main] ManagedManagementStrategy      INFO  JMX is enabled
[                          main] DefaultTypeConverter           INFO  Type converters loaded (core: 192, classpath: 0)
[                          main] SpringCamelContext             INFO  StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
[                          main] SpringCamelContext             INFO  Route: bar started and consuming from: direct://bar
[                          main] SpringCamelContext             INFO  Route: csv started and consuming from: file://data
[                          main] SpringCamelContext             INFO  Total 2 routes, of which 2 are started
[                          main] SpringCamelContext             INFO  Apache Camel 2.20.2 (CamelContext: camel_xml_dsl) started in 0.294 seconds
Feb 12, 2018 12:48:54 PM org.springframework.context.support.DefaultLifecycleProcessor start
INFO: Starting beans in phase 2147483646
[                          main] FileWatcherReloadStrategy      INFO  Starting ReloadStrategy to watch directory: routes
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO  Received in message with size 3543
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO    Trx 1. request type : Purchase, request time : 2017-06-30 14:47:54, instant : 2017-06-30T12:47:54Z
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO    Trx 2. request type : Purchase, request time : 2017-06-30 14:48:03, instant : 2017-06-30T12:48:03Z
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO    Trx 3. request type : Purchase, request time : 2018-02-01 14:18:12, instant : 2018-02-01T13:18:12Z
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO    Trx 4. request type : Purchase, request time : 2018-02-02 11:13:46, instant : 2018-02-02T10:13:46Z
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO    Trx 5. request type : Purchase, request time : 2018-02-05 16:04:27, instant : 2018-02-05T15:04:27Z
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO    Trx 6. request type : Purchase, request time : 2018-02-05 16:40:39, instant : 2018-02-05T15:40:39Z
[l_dsl) thread #2 - file://data] CsvProcessor                   INFO    Trx 7. request type : Purchase, request time : 2018-02-06 12:32:20, instant : 2018-02-06T11:32:20Z
[l_dsl) thread #2 - file://data] AccessoryOrderProcessor        INFO  Received in message with 7 trx, paper per trx 14
[l_dsl) thread #2 - file://data] AccessoryOrderProcessor        INFO  Paper consumed 98
[#3 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      WARN  Cannot load the resource C:\Users\moro\temp\camel\routes\camel-context.xml as XML
[#3 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Starting to graceful shutdown 1 routes (timeout 300 seconds)
[_dsl) thread #4 - ShutdownTask] DefaultShutdownStrategy        INFO  Route: csv shutdown complete, was consuming from: file://data
[#3 - FileWatcherReloadStrategy] DefaultShutdownStrategy        INFO  Graceful shutdown of 1 routes completed in 0 seconds
[#3 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv is stopped, was consuming from: file://data
[#3 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv is shutdown and removed, was consuming from: file://data
[#3 - FileWatcherReloadStrategy] SpringCamelContext             INFO  Route: csv started and consuming from: file://data
[#3 - FileWatcherReloadStrategy] FileWatcherReloadStrategy      INFO  Reloaded routes: [csv] from XML resource: C:\Users\moro\temp\camel\routes\camel-context.xml
[l_dsl) thread #5 - file://data] CsvProcessor                   INFO  Received in message with size 3543
[l_dsl) thread #5 - file://data] CsvProcessor                   INFO    Trx 1. request type : Purchase, request time : 2017-06-30 14:47:54, instant : 2017-06-30T12:47:54Z
[l_dsl) thread #5 - file://data] CsvProcessor                   INFO    Trx 2. request type : Purchase, request time : 2017-06-30 14:48:03, instant : 2017-06-30T12:48:03Z
[l_dsl) thread #5 - file://data] CsvProcessor                   INFO    Trx 3. request type : Purchase, request time : 2018-02-01 14:18:12, instant : 2018-02-01T13:18:12Z
[l_dsl) thread #5 - file://data] CsvProcessor                   INFO    Trx 4. request type : Purchase, request time : 2018-02-02 11:13:46, instant : 2018-02-02T10:13:46Z
[l_dsl) thread #5 - file://data] CsvProcessor                   INFO    Trx 5. request type : Purchase, request time : 2018-02-05 16:04:27, instant : 2018-02-05T15:04:27Z
[l_dsl) thread #5 - file://data] CsvProcessor                   INFO    Trx 6. request type : Purchase, request time : 2018-02-05 16:40:39, instant : 2018-02-05T15:40:39Z
[l_dsl) thread #5 - file://data] CsvProcessor                   INFO    Trx 7. request type : Purchase, request time : 2018-02-06 12:32:20, instant : 2018-02-06T11:32:20Z
```
