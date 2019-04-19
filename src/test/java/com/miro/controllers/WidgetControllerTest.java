package com.miro.controllers;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(WidgetControllerTest.class)
@Suite.SuiteClasses({ WidgetControllerTest.CreateWidget.class})
public class WidgetControllerTest extends Suite {

    public WidgetControllerTest(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(klass, builder);
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest
    @AutoConfigureWebTestClient
    @Category(WidgetControllerTest.class)
    public static class CreateWidget{

        @Test
        public void should_throw_widget_not_found_exception_when_pass_invalid_guid() {

        }
    }
}
