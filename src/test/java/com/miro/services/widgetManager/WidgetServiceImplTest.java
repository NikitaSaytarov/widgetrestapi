package com.miro.services.widgetManager;

import com.miro.core.data.internal.WidgetInternal;
import com.miro.core.dto.WidgetDto;
import com.miro.core.exceptions.WidgetNotFoundException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;
import static org.springframework.test.util.AssertionErrors.assertTrue;

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

        @DataPoints("InputParametersSet1")
        public static Double[] InputParametersSet1() {
            Double [] doubles = {null, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY , -5d, -200d, 0d, 300d, 1d};
            System.out.println("Generated test data: "
                    + Arrays.toString(doubles));
            return doubles;
        }
        @DataPoints("InputParametersSet2")
        public static Integer[] InputParametersSet2() {
            Integer [] integers = {null, -4, 100, Integer.MIN_VALUE, 0, 2, 6};
            System.out.println("Generated test data: "
                    + Arrays.toString(integers));
            return integers;
        }
        @DataPoints("InputParametersSet3")
        public static Double[] InputParametersSet3() {
            Double [] doubles = {5d, 200d, 1d, 40d};
            System.out.println("Generated test data: "
                    + Arrays.toString(doubles));
            return doubles;
        }

        @Theory
        public void should_throw_illegal_argument_exception_when_pass_invalid_parameters_x_y_width_height(@FromDataPoints("InputParametersSet1") Double  parameter) {
            //Arrange
            assumeThat(parameter,either(is(Double.NaN)).or(is(Double.NEGATIVE_INFINITY)).or(is(Double.POSITIVE_INFINITY)).or(is(lessThan(0d))).or(nullValue()));
            var sut = new WidgetServiceImpl();
            var correctValue = 6d;

            //Assert
            //Act
            assertThatIllegalArgumentException().isThrownBy(() -> { sut.createWidget(parameter,correctValue,correctValue,correctValue, null);});
            assertThatIllegalArgumentException().isThrownBy(() -> { sut.createWidget(correctValue,parameter,correctValue,correctValue,null);});
            assertThatIllegalArgumentException().isThrownBy(() -> { sut.createWidget(correctValue,correctValue,parameter,correctValue,null);});
            assertThatIllegalArgumentException().isThrownBy(() -> { sut.createWidget(correctValue,correctValue,correctValue,parameter,null);});
        }

        @Theory
        public void should_throw_illegal_argument_exception_when_pass_invalid_parameters_z_index(@FromDataPoints("InputParametersSet2") Integer  zIndex) {
            //Arrange
            assumeThat(zIndex,lessThan(0));
            var sut = new WidgetServiceImpl();
            var correctValue = 6d;

            //Assert
            //Act
            assertThatIllegalArgumentException().isThrownBy(() -> { sut.createWidget(correctValue,correctValue,correctValue,correctValue, zIndex);});
        }

        @Theory
        public void should_return_successful_created_widgetDto_with_specific_properties_when_pass_specific_parameters(@FromDataPoints("InputParametersSet3") Double  xParameter,
                                                                                                                      @FromDataPoints("InputParametersSet3") Double  yParameter,
                                                                                                                      @FromDataPoints("InputParametersSet3") Double  widthParameter,
                                                                                                                      @FromDataPoints("InputParametersSet3") Double  heightParameter,
                                                                                                                      @FromDataPoints("InputParametersSet2") Integer  zIndexParameter) {
            //Arrange
            assumeThat(zIndexParameter,greaterThanOrEqualTo(0));
            var sut = new WidgetServiceImpl();

            //Act
            var widgetDto  = sut.createWidget(xParameter,yParameter,widthParameter,heightParameter, zIndexParameter);

            //Assert
            assertEquals(widgetDto.getX(), xParameter);
            assertEquals(widgetDto.getY(), yParameter);
            assertEquals(widgetDto.getWidth(), widthParameter);
            assertEquals(widgetDto.getHeight(), heightParameter);
            assertEquals(widgetDto.getzIndex(), zIndexParameter);
        }

        @Test
        public void should_return_created_widgetDto_with_zIndex_max_in_collection_when_no_pass_zIndex_parameter() {

            //Arrange
            var validX = 1d;
            var validY = 1d;
            var validWidth = 1d;
            var validHeight = 1d;
            var sut = new WidgetServiceImpl();
            var firstElementZIndex = 2;
            var lastElementZIndex = 5;
            sut.createWidget(validX,validY,validWidth,validHeight, firstElementZIndex);
            sut.createWidget(validX,validY,validWidth,validHeight, lastElementZIndex);

            //Act
            var widgetDto  = sut.createWidget(validX,validY,validWidth,validHeight, null);

            //Assert
            assertTrue("Error, zIndex wrong ", widgetDto.getzIndex() == lastElementZIndex + 1);
        }

        @Test
        public void should_shift_z_index_in_collection_when_pass_existing_zIndex_parameter() {

            //Arrange
            var validX = 1d;
            var validY = 1d;
            var validWidth = 1d;
            var validHeight = 1d;

            var zIndex1 = 5;
            var zIndex2 = 6;
            var zIndex3 = 7;
            var zIndex4 = 8;
            var zIndex5 = 44;
            var zIndex6 = 555;

            var zIndex = 7;

            var sut = new WidgetServiceImpl();
            var widget1 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex1);
            var widget2 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex2);
            var widget3 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex3);
            var widget4 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex4);
            var widget5 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex5);
            var widget6 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex6);

            //Act
            var widgetDto  = sut.createWidget(validX,validY,validWidth,validHeight, zIndex);

            //Assert
            assertThatCode(() -> {
                var widget1Dto = sut.getWidget(widget1.getGuid());
                assertTrue("Error, zIndex wrong ", widget1Dto.getzIndex() == zIndex1);

                var widget2Dto = sut.getWidget(widget2.getGuid());
                assertTrue("Error, zIndex wrong ", widget2Dto.getzIndex() == zIndex2);

                var widgetMainDto = sut.getWidget(widgetDto.getGuid());
                assertTrue("Error, zIndex wrong ", widgetMainDto.getzIndex() == zIndex);

                var widget3Dto = sut.getWidget(widget3.getGuid());
                assertTrue("Error, zIndex wrong ", widget3Dto.getzIndex() == zIndex3 + 1);

                var widget4Dto = sut.getWidget(widget4.getGuid());
                assertTrue("Error, zIndex wrong ", widget4Dto.getzIndex() == zIndex4  + 1);

                var widget5Dto = sut.getWidget(widget5.getGuid());
                assertTrue("Error, zIndex wrong ", widget5Dto.getzIndex() == zIndex5  + 1);

                var widget6Dto = sut.getWidget(widget6.getGuid());
                assertTrue("Error, zIndex wrong ", widget6Dto.getzIndex() == zIndex6  + 1);
            }).doesNotThrowAnyException();
        }
    }

    @RunWith(Theories.class)
    @Category(WidgetServiceImplTest.class)
    public static class getWidget{
        @Test
        public void should_return_widgetDto_when_pass_widget_guid_parameter() {

            //Arrange
            var sut = new WidgetServiceImpl();

            var validX = 1d;
            var validY = 1d;
            var validWidth = 1d;
            var validHeight = 1d;
            var firstElementZIndex = 2;
            var lastElementZIndex = 5;
            sut.createWidget(validX,validY,validWidth,validHeight, firstElementZIndex + 1);
            var widgetDto = sut.createWidget(validX,validY,validWidth,validHeight, firstElementZIndex);
            sut.createWidget(validX,validY,validWidth,validHeight, lastElementZIndex);

            var guid = widgetDto.getGuid();
            assertThatCode(() -> {
                //Act
                var widget  = sut.getWidget(guid);

                //Assert
                assertTrue("Error", widget.getGuid() == guid);
            }).doesNotThrowAnyException();
        }
        @Test
        public void should_throw_illegal_argument_exception_when_try_get_widget_with_no_exist_guid() {

            //Arrange
            var sut = new WidgetServiceImpl();

            var validX = 1d;
            var validY = 1d;
            var validWidth = 1d;
            var validHeight = 1d;
            var firstElementZIndex = 2;
            var lastElementZIndex = 5;
            sut.createWidget(validX,validY,validWidth,validHeight, firstElementZIndex + 1);
            sut.createWidget(validX,validY,validWidth,validHeight, firstElementZIndex);
            sut.createWidget(validX,validY,validWidth,validHeight, lastElementZIndex);

            //Act
            //Assert
            assertThatExceptionOfType(WidgetNotFoundException.class).isThrownBy(() -> { sut.getWidget(UUID.randomUUID());});
        }
    }
}