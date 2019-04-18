package com.miro.services.widgetManager;

import org.junit.experimental.categories.Category;
import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

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

        //@DataPoints("a values")
        //public static int[] aValues() {
        //    // Use new Random().nextInt() to experiment:
        //    int[] ints = {1, 2};
        //    System.out.println("Generated test data: "
        //            + Arrays.toString(ints));
        //    return ints;
        //}
        //
        //@DataPoints("b values")
        //public static int[] bValues() {
        //    // Generate dynamically:
        //    int[] ints = {3, 4};
        //    System.out.println("Generated test data: "
        //            + Arrays.toString(ints));
        //    return ints;
        //}
        //
        //@Theory
        //public void sumShouldBeCommutative(@FromDataPoints("a values") int a,
        //                                   @FromDataPoints("b values") int b) {
        //    System.out.printf("a = %d, b = %d%n", a, b);
        //    assertThat(a + b).isNotEqualTo(b + a);
        //}

        @Theory
        public void should_throw_illegal_argument_exception_when_pass_invalid_parameters()
        {
            //Arrange
            //Act
            //Assert
        }
    }
}