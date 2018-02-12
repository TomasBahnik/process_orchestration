/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example.reload;

import org.apache.camel.spring.Main;

/**
 * A main class to run the example from your editor.
 */
public final class CamelReloadMain {

    public static final String SPRING_CAMEL_CONTEXT_XML = "META-INF/spring/camel-context.xml";
    public static final String WATCH_DIRECTORY = "src/main/resources/META-INF/spring";
    private static final String SPRING_CAMEL_CONTEXT_XML_KEY = "camel.context";
    private static final String WATCH_DIRECTORY_KEY = "camel.watch.directory";

    private CamelReloadMain() {
    }

    public static void main(String[] args) throws Exception {
        String camelContext = System.getProperty(SPRING_CAMEL_CONTEXT_XML_KEY, SPRING_CAMEL_CONTEXT_XML);
        String camelWatchDirectory = System.getProperty(WATCH_DIRECTORY_KEY, WATCH_DIRECTORY);
        System.out.printf("Camel context : %s%n", camelContext);
        System.out.printf("Camel watch directory : %s%n", camelWatchDirectory);
        // Main makes it easier to run a Spring application
        Main main = new Main();
        // configure the location of the Spring XML file
        main.setApplicationContextUri(camelContext);
        // turn on reload when the XML file is updated in the source code
        main.setFileWatchDirectory(camelWatchDirectory);
        // run and block until Camel is stopped (or JVM terminated)
        main.run();
    }
}
