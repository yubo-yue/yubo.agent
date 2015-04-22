#!/bin/sh
java -javaagent:../simple-agent-metric/target/simple-agent-metric-jar-with-dependencies.jar -cp target/simple-agent-test-jar-with-dependencies.jar org.simple.agent.test.RunExample
