package com.miro.services.widgetManager;

import com.nitorcreations.junit.runners.NestedRunner;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.Theory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class WidgetServiceImplTest
{
    @Nested
    @Category(WidgetServiceImplTest.class)
    public class createWidget{

        @Theory
        public void should_throw_illegal_argument_exception_when_pass_invalid_parameters()
        {
            //Arrange
            //Act
            //Assert
        }
    }
}