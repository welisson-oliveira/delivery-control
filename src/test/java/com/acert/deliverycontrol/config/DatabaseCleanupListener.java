package com.acert.deliverycontrol.config;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class DatabaseCleanupListener extends AbstractTestExecutionListener {

    private boolean alreadyCleared;

    public DatabaseCleanupListener() {
        this.alreadyCleared = false;
    }

    @Override
    public final int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void prepareTestInstance(final TestContext testContext) throws Exception {
        if (!this.alreadyCleared) {
            this.cleanupDatabase(testContext);
        }

        this.alreadyCleared = true;
    }

    @Override
    public void afterTestMethod(final TestContext testContext) throws Exception {
        this.cleanupDatabase(testContext);
        super.afterTestMethod(testContext);
    }

    @Override
    public void afterTestClass(final TestContext testContext) throws Exception {
        this.cleanupDatabase(testContext);
    }

    private void cleanupDatabase(final TestContext testContext) throws LiquibaseException {
        final ApplicationContext app = testContext.getApplicationContext();
        final SpringLiquibase springLiquibase = app.getBean(SpringLiquibase.class);
        springLiquibase.setDropFirst(true);
        springLiquibase.afterPropertiesSet();
    }
}

