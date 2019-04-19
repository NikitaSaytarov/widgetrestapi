package com.miro.services.widgetManager;

import com.miro.core.data.internal.WidgetInternal;
import com.miro.core.data.internal.WidgetLayoutInfo;
import com.miro.core.dto.WidgetDto;
import com.miro.core.exceptions.WidgetNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(WidgetServiceImplTest.class)
@Suite.SuiteClasses({ WidgetServiceImplTest.createWidget.class,
        WidgetServiceImplTest.updateWidget.class,
        WidgetServiceImplTest.getWidget.class,
        WidgetServiceImplTest.getAllWidgets.class })
public class WidgetServiceImplTest  extends Suite
{
    public static class TheoryParametersFixture{
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
    }

    public WidgetServiceImplTest(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(klass, builder);
    }

    @RunWith(Theories.class)
    @Category(WidgetServiceImplTest.class)
    public static class createWidget{

        @DataPoints("InputParametersSet1")
        public static Double[] InputParametersSet1() {
            return WidgetServiceImplTest.TheoryParametersFixture.InputParametersSet1();
        }
        @DataPoints("InputParametersSet2")
        public static Integer[] InputParametersSet2() {
            return WidgetServiceImplTest.TheoryParametersFixture.InputParametersSet2();
        }
        @DataPoints("InputParametersSet3")
        public static Double[] InputParametersSet3() {
            return WidgetServiceImplTest.TheoryParametersFixture.InputParametersSet3();
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
        public void should_throw_widget_not_found_exception_when_try_get_widget_with_no_exist_guid() {

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

    @RunWith(Theories.class)
    @Category(WidgetServiceImplTest.class)
    public static class updateWidget{

        @DataPoints("InputParametersSet1")
        public static Double[] InputParametersSet1() {
            return WidgetServiceImplTest.TheoryParametersFixture.InputParametersSet1();
        }
        @DataPoints("InputParametersSet2")
        public static Integer[] InputParametersSet2() {
            return WidgetServiceImplTest.TheoryParametersFixture.InputParametersSet2();
        }
        @DataPoints("InputParametersSet3")
        public static Double[] InputParametersSet3() {
            return WidgetServiceImplTest.TheoryParametersFixture.InputParametersSet3();
        }

        @Test
        public void should_throw_null_pointer_exception_when_pass_null_parameters() {

            //Arrange
            var sut = new WidgetServiceImpl();

            //Act
            //Assert
            assertThatNullPointerException().isThrownBy(() -> { sut.updateWidget(null,null);});
            assertThatNullPointerException().isThrownBy(() -> { sut.updateWidget(UUID.randomUUID(),null);});
            assertThatNullPointerException().isThrownBy(() -> { sut.updateWidget(null,new WidgetLayoutInfo());});
        }

        @Test
        public void should_throw_widget_not_found_exception_when_try_update_widget_with_no_exist_guid() {

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

            var widgetLayoutInfo = new WidgetLayoutInfo();
            widgetLayoutInfo.setzIndex(3);
            widgetLayoutInfo.setX(3);
            widgetLayoutInfo.setY(3);
            widgetLayoutInfo.setWidth(3);
            widgetLayoutInfo.setHeight(3);

            //Act
            //Assert
            assertThatExceptionOfType(WidgetNotFoundException.class).isThrownBy(() -> { sut.updateWidget(UUID.randomUUID() ,widgetLayoutInfo);});
        }

        @Theory
        public void should_return_successful_updated_widgetDto_when_pass_all_parameters(@FromDataPoints("InputParametersSet3") Double  xParameter,
                                                                                              @FromDataPoints("InputParametersSet3") Double  yParameter,
                                                                                              @FromDataPoints("InputParametersSet3") Double  widthParameter,
                                                                                              @FromDataPoints("InputParametersSet3") Double  heightParameter,
                                                                                              @FromDataPoints("InputParametersSet2") Integer  zIndexParameter) {
            //Arrange
            assumeThat(zIndexParameter,greaterThanOrEqualTo(0));
            var sut = new WidgetServiceImpl();
            var createdWidget  = sut.createWidget(1,1,1,1, 1);
            var uuid = createdWidget.getGuid();
            var newWidgetLayoutInfo = new WidgetLayoutInfo();
            newWidgetLayoutInfo.setX(xParameter);
            newWidgetLayoutInfo.setY(yParameter);
            newWidgetLayoutInfo.setWidth(widthParameter);
            newWidgetLayoutInfo.setHeight(heightParameter);
            newWidgetLayoutInfo.setzIndex(zIndexParameter);

            assertThatCode(() -> {
                //Act
                //Assert
                sut.updateWidget(uuid, newWidgetLayoutInfo);
                var updatedWidget  = sut.getWidget(uuid);

                assertEquals(updatedWidget.getX(), xParameter);
                assertEquals(updatedWidget.getY(), yParameter);
                assertEquals(updatedWidget.getWidth(), widthParameter);
                assertEquals(updatedWidget.getHeight(), heightParameter);
                assertEquals(updatedWidget.getzIndex(), zIndexParameter);
            }).doesNotThrowAnyException();
        }

        @Theory
        public void should_return_successful_updated_widgetDto_when_pass_not_all_parameters(@FromDataPoints("InputParametersSet3") Double  xParameter,
                                                                                        @FromDataPoints("InputParametersSet3") Double  yParameter,
                                                                                        @FromDataPoints("InputParametersSet3") Double  widthParameter,
                                                                                        @FromDataPoints("InputParametersSet3") Double  heightParameter,
                                                                                        @FromDataPoints("InputParametersSet2") Integer  zIndexParameter) {
            //Arrange
            assumeThat(zIndexParameter,greaterThanOrEqualTo(0));
            var sut = new WidgetServiceImpl();

            Double initialX = 1d;
            Double initialY = 1d;
            Double initialWidth = 1d;
            Double initialHeight = 1d;

            Integer initialZIndex1 = 1;
            Integer initialZIndex2 = 2;
            Integer initialZIndex3 = 3;
            Integer initialZIndex4 = 4;
            Integer initialZIndex5 = 5;

            var createdWidget1  = sut.createWidget(initialX,initialY,initialWidth,initialHeight, initialZIndex1);
            var createdWidget2  = sut.createWidget(initialX,initialY,initialWidth,initialHeight, initialZIndex2);
            var createdWidget3  = sut.createWidget(initialX,initialY,initialWidth,initialHeight, initialZIndex3);
            var createdWidget4  = sut.createWidget(initialX,initialY,initialWidth,initialHeight, initialZIndex4);
            var createdWidget5  = sut.createWidget(initialX,initialY,initialWidth,initialHeight, initialZIndex5);

            var uuid1 = createdWidget1.getGuid();
            var uuid2 = createdWidget2.getGuid();
            var uuid3 = createdWidget3.getGuid();
            var uuid4 = createdWidget4.getGuid();
            var uuid5 = createdWidget5.getGuid();

            var new1 = new WidgetLayoutInfo();
            new1.setX(xParameter);
            assertThatCode(() -> {
                //Act
                //Assert
                sut.updateWidget(uuid1, new1);
                var updatedWidget  = sut.getWidget(uuid1);

                assertEquals(updatedWidget.getX(), xParameter);
                assertEquals(updatedWidget.getY(), initialY);
                assertEquals(updatedWidget.getWidth(), initialWidth);
                assertEquals(updatedWidget.getHeight(), initialHeight);
                assertEquals(updatedWidget.getzIndex(), initialZIndex1);
            }).doesNotThrowAnyException();

            var new2 = new WidgetLayoutInfo();
            new2.setY(yParameter);
            assertThatCode(() -> {
                //Act
                //Assert
                sut.updateWidget(uuid2, new2);
                var updatedWidget  = sut.getWidget(uuid2);

                assertEquals(updatedWidget.getX(), initialX);
                assertEquals(updatedWidget.getY(), yParameter);
                assertEquals(updatedWidget.getWidth(), initialWidth);
                assertEquals(updatedWidget.getHeight(), initialHeight);
                assertEquals(updatedWidget.getzIndex(), initialZIndex2);
            }).doesNotThrowAnyException();

            var new3 = new WidgetLayoutInfo();
            new3.setWidth(widthParameter);
            assertThatCode(() -> {
                //Act
                //Assert
                sut.updateWidget(uuid3, new3);
                var updatedWidget  = sut.getWidget(uuid3);

                assertEquals(updatedWidget.getX(), initialX);
                assertEquals(updatedWidget.getY(), initialY);
                assertEquals(updatedWidget.getWidth(), widthParameter);
                assertEquals(updatedWidget.getHeight(), initialHeight);
                assertEquals(updatedWidget.getzIndex(), initialZIndex3);
            }).doesNotThrowAnyException();

            var new4 = new WidgetLayoutInfo();
            new4.setHeight(heightParameter);
            assertThatCode(() -> {
                //Act
                //Assert
                sut.updateWidget(uuid4, new4);
                var updatedWidget  = sut.getWidget(uuid4);

                assertEquals(updatedWidget.getX(), initialX);
                assertEquals(updatedWidget.getY(), initialY);
                assertEquals(updatedWidget.getWidth(), initialWidth);
                assertEquals(updatedWidget.getHeight(), heightParameter);
                assertEquals(updatedWidget.getzIndex(), initialZIndex4);
            }).doesNotThrowAnyException();

            var new5 = new WidgetLayoutInfo();
            new5.setzIndex(zIndexParameter);
            assertThatCode(() -> {
                //Act
                //Assert
                sut.updateWidget(uuid5, new5);
                var updatedWidget  = sut.getWidget(uuid5);

                assertEquals(updatedWidget.getX(), initialX);
                assertEquals(updatedWidget.getY(), initialY);
                assertEquals(updatedWidget.getWidth(), initialWidth);
                assertEquals(updatedWidget.getHeight(), initialHeight);
                assertEquals(updatedWidget.getzIndex(), zIndexParameter);
            }).doesNotThrowAnyException();
        }

        @Test
        public void should_shift_z_index_in_collection_when_update_widget_and_pass_existing_zIndex_parameter() {

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

            var zIndex = 8;

            var sut = new WidgetServiceImpl();
            var widget1 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex1);
            var widget2 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex2);
            var widget3 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex3);
            var widget4 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex4);
            var widget5 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex5);
            var widget6 = sut.createWidget(validX,validY,validWidth,validHeight, zIndex6);

            var updatedWidgetLayoutInfo = new WidgetLayoutInfo();
            updatedWidgetLayoutInfo.setX(1);
            updatedWidgetLayoutInfo.setY(1);
            updatedWidgetLayoutInfo.setWidth(1);
            updatedWidgetLayoutInfo.setHeight(1);
            updatedWidgetLayoutInfo.setzIndex(zIndex);

            assertThatCode(() -> {
                //Act
                sut.updateWidget(widget3.getGuid(),updatedWidgetLayoutInfo);

                var widget1Dto = sut.getWidget(widget1.getGuid());
                // Assert
                assertTrue("Error, zIndex wrong ", widget1Dto.getzIndex() == zIndex1);

                var widget2Dto = sut.getWidget(widget2.getGuid());
                // Assert
                assertTrue("Error, zIndex wrong ", widget2Dto.getzIndex() == zIndex2);

                var widgetMainDto = sut.getWidget(widget3.getGuid());
                // Assert
                assertTrue("Error, zIndex wrong ", widgetMainDto.getzIndex() == zIndex);

                var widget4Dto = sut.getWidget(widget4.getGuid());
                // Assert
                assertTrue("Error, zIndex wrong ", widget4Dto.getzIndex() == zIndex4  + 1);

                var widget5Dto = sut.getWidget(widget5.getGuid());
                // Assert
                assertTrue("Error, zIndex wrong ", widget5Dto.getzIndex() == zIndex5  + 1);

                var widget6Dto = sut.getWidget(widget6.getGuid());
                // Assert
                assertTrue("Error, zIndex wrong ", widget6Dto.getzIndex() == zIndex6  + 1);
            }).doesNotThrowAnyException();
        }

        @Test
        public void should_change_updated_at_when_update_widget() {

            //Arrange
            var validX = 1d;
            var validY = 1d;
            var validWidth = 1d;
            var validHeight = 1d;
            var zIndex = 1;

            var sut = new WidgetServiceImpl();
            var widget = sut.createWidget(validX,validY,validWidth,validHeight, zIndex);

            var updatedWidgetLayoutInfo1 = new WidgetLayoutInfo();
            updatedWidgetLayoutInfo1.setX(validX);
            updatedWidgetLayoutInfo1.setY(validY);
            updatedWidgetLayoutInfo1.setWidth(validWidth);
            updatedWidgetLayoutInfo1.setHeight(validHeight);
            updatedWidgetLayoutInfo1.setzIndex(zIndex + 1);

            var updatedWidgetLayoutInfo2 = new WidgetLayoutInfo();
            updatedWidgetLayoutInfo2.setX(validX + 1);
            updatedWidgetLayoutInfo2.setY(validY);
            updatedWidgetLayoutInfo2.setWidth(validWidth);
            updatedWidgetLayoutInfo2.setHeight(validHeight);
            updatedWidgetLayoutInfo2.setzIndex(zIndex + 1);

            var updatedWidgetLayoutInfo3 = new WidgetLayoutInfo();
            updatedWidgetLayoutInfo3.setX(validX);
            updatedWidgetLayoutInfo3.setY(validY + 1);
            updatedWidgetLayoutInfo3.setWidth(validWidth);
            updatedWidgetLayoutInfo3.setHeight(validHeight);
            updatedWidgetLayoutInfo3.setzIndex(zIndex);

            var updatedWidgetLayoutInfo4 = new WidgetLayoutInfo();
            updatedWidgetLayoutInfo4.setX(validX);
            updatedWidgetLayoutInfo4.setY(validY);
            updatedWidgetLayoutInfo4.setWidth(validWidth + 1);
            updatedWidgetLayoutInfo4.setHeight(validHeight);
            updatedWidgetLayoutInfo4.setzIndex(zIndex);

            var updatedWidgetLayoutInfo5 = new WidgetLayoutInfo();
            updatedWidgetLayoutInfo5.setX(validX);
            updatedWidgetLayoutInfo5.setY(validY);
            updatedWidgetLayoutInfo5.setWidth(validWidth);
            updatedWidgetLayoutInfo5.setHeight(validHeight + 1);
            updatedWidgetLayoutInfo5.setzIndex(zIndex);

            assertThatCode(() -> {
                WidgetDto widgetDto;
                LocalDateTime updatedAtUtc = widget.getUpdatedAtUtc();

                //Act
                sut.updateWidget(widget.getGuid(),updatedWidgetLayoutInfo1);

                // Assert
                widgetDto = sut.getWidget(widget.getGuid());
                assertTrue("Error, zIndex wrong ", widgetDto.getUpdatedAtUtc().isAfter(updatedAtUtc));

                //Act
                updatedAtUtc = widget.getUpdatedAtUtc();
                sut.updateWidget(widget.getGuid(),updatedWidgetLayoutInfo2);

                // Assert
                widgetDto = sut.getWidget(widget.getGuid());
                assertTrue("Error, zIndex wrong ", widgetDto.getUpdatedAtUtc().isAfter(updatedAtUtc));

                //Act
                updatedAtUtc = widget.getUpdatedAtUtc();
                sut.updateWidget(widget.getGuid(),updatedWidgetLayoutInfo3);

                // Assert
                widgetDto = sut.getWidget(widget.getGuid());
                assertTrue("Error, zIndex wrong ", widgetDto.getUpdatedAtUtc().isAfter(updatedAtUtc));

                //Act
                updatedAtUtc = widget.getUpdatedAtUtc();
                sut.updateWidget(widget.getGuid(),updatedWidgetLayoutInfo4);

                // Assert
                widgetDto = sut.getWidget(widget.getGuid());
                assertTrue("Error, zIndex wrong ", widgetDto.getUpdatedAtUtc().isAfter(updatedAtUtc));

                //Act
                updatedAtUtc = widget.getUpdatedAtUtc();
                sut.updateWidget(widget.getGuid(),updatedWidgetLayoutInfo5);

                // Assert
                widgetDto = sut.getWidget(widget.getGuid());
                assertTrue("Error, zIndex wrong ", widgetDto.getUpdatedAtUtc().isAfter(updatedAtUtc));

            }).doesNotThrowAnyException();
        }
    }


    @RunWith(Theories.class)
    @Category(WidgetServiceImplTest.class)
    public static class getAllWidgets{

        @Test
        public void should_return_all_widgets_sorted_by_zIndex() {

            //Arrange
            var sut = new WidgetServiceImpl();

            var validX = 1d;
            var validY = 1d;
            var validWidth = 1d;
            var validHeight = 1d;
            sut.createWidget(validX,validY,validWidth,validHeight, 1);
            sut.createWidget(validX,validY,validWidth,validHeight, 300);
            sut.createWidget(validX,validY,validWidth,validHeight, 2);
            sut.createWidget(validX,validY,validWidth,validHeight, 6);
            sut.createWidget(validX,validY,validWidth,validHeight, 300);
            sut.createWidget(validX,validY,validWidth,validHeight, 0);
            sut.createWidget(validX,validY,validWidth,validHeight, 1);

            //Act
            var widgets = sut.getAllWidgets();

            Integer lastZIndex = -1;
            for (var widget : widgets){
                //Assert
                assertTrue("Error", widget.getzIndex() >  lastZIndex);
                lastZIndex = widget.getzIndex();
            }
        }
    }
}