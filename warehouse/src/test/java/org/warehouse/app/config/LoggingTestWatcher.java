package org.warehouse.app.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.MDC;

@Slf4j
public class LoggingTestWatcher extends TestWatcher {

    @Override
    protected void starting(Description description) {
        log.info("test {} stated", createFullMethodName(description.getClassName(), description.getMethodName()));
        MDC.put("testName", createFullMethodName(description.getClassName(), description.getMethodName()));
    }

    @Override
    protected void finished(Description description) {
        MDC.clear();
        log.info("test {} finished", createFullMethodName(description.getClassName(), description.getMethodName()));
    }

    private String createFullMethodName(String className, String methodName) {
        return String.format("%s.%s", className, methodName);
    }
}
