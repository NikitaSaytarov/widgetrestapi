package com.miro.services.widgetManager;

import org.junit.experimental.categories.Category;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

@RunWith(WidgetServiceImplTest.class)
@Suite.SuiteClasses({ WidgetServiceImplTest.createWidget.class })
public class WidgetServiceImplTest  extends Suite
{
    public WidgetServiceImplTest(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(klass, builder);
    }

    @RunWith(Theories.class)
    @Category(WidgetServiceImplTest.class)
    public static class createWidget{
        //@Theory
        //public void should_throw_illegal_argument_exception_when_pass_invalid_parameters()
        //{
        //    //Arrange
        //    //Act
        //    //Assert
        //}
    }
}