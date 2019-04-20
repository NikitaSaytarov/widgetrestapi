package com.miro.controllers;

import com.miro.core.data.internal.WidgetLayoutInfo;
import com.miro.core.dto.WidgetDto;
import com.miro.core.exceptions.WidgetNotFoundException;
import com.miro.services.widgetManager.WidgetServiceImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(WidgetControllerTest.class)
@Suite.SuiteClasses({ WidgetControllerTest.CreateWidget.class,
        WidgetControllerTest.GetWidget.class,
        WidgetControllerTest.UpdateWidget.class})
public class WidgetControllerTest extends Suite {

    public WidgetControllerTest(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(klass, builder);
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest
    @AutoConfigureMockMvc
    @Category(WidgetControllerTest.class)
    public static class CreateWidget{

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private WidgetServiceImpl widgetService;

        @Test
        public void should_return_201_code_when_pass_valid_parameters() {

            //Arrange
            var widgetDtoStub = new WidgetDto();
            widgetDtoStub.setGuid(UUID.randomUUID());
            when(widgetService.createWidget(Mockito.any(Double.class),
                    Mockito.any(Double.class),
                    Mockito.any(Double.class),
                    Mockito.any(Double.class),
                    Mockito.any(Integer.class)))
                    .thenReturn(widgetDtoStub);

            assertThatCode(() -> {
                //Act
                //Assert
                mockMvc.perform(post("/api/v1/widgets/add")
                        .param("x", "1")
                        .param("y", "1")
                        .param("width","1")
                        .param("height", "1")
                        .param("zIndex", "1"))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.guid").exists())
                        //.andExpect(jsonPath("$.zIndex").exists())
                        //.andExpect(jsonPath("$.x").exists())
                        //.andExpect(jsonPath("$.y").exists())
                        //.andExpect(jsonPath("$.width").exists())
                        //.andExpect(jsonPath("$.height").exists())
                        //.andExpect(jsonPath("$.updatedAtUtc").exists())
                        .andExpect(content().contentType("application/json"));
            }).doesNotThrowAnyException();
        }

        @Test
        public void should_return_200_code_when_pass_invalid_parameters() {

            //Arrange
            var widgetDtoStub = new WidgetDto();
            widgetDtoStub.setGuid(UUID.randomUUID());
            when(widgetService.createWidget(Mockito.any(Double.class),
                    Mockito.any(Double.class),
                    Mockito.any(Double.class),
                    Mockito.any(Double.class),
                    Mockito.any(Integer.class)))
                    .thenReturn(widgetDtoStub);

            assertThatCode(() -> {
                //Act
                //Assert
                mockMvc.perform(post("/api/v1/widgets/add")
                        .param("x", String.valueOf(Double.NEGATIVE_INFINITY))
                        .param("y", String.valueOf(Double.POSITIVE_INFINITY))
                        .param("width",String.valueOf(Double.NaN))
                        .param("height", "null")
                        .param("zIndex", "1"))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }).doesNotThrowAnyException();
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest
    @AutoConfigureMockMvc
    @Category(WidgetControllerTest.class)
    public static class GetWidget{

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private WidgetServiceImpl widgetService;

        @Test
        public void should_return_200_code_when_pass_valid_parameters() throws WidgetNotFoundException {

            //Arrange
            var widgetDtoStub = new WidgetDto();
            var guid = UUID.randomUUID();
            widgetDtoStub.setGuid(guid);
            when(widgetService.getWidget(Mockito.any(UUID.class)))
                    .thenReturn(widgetDtoStub);

            assertThatCode(() -> {
                //Act
                //Assert
                mockMvc.perform(get("/api/v1/widgets/" + guid.toString()))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.guid").exists())
                        .andExpect(content().contentType("application/json"));
            }).doesNotThrowAnyException();
        }

        @Test
        public void should_return_404_code_when_pass_guid_and_widget_not_exist() throws WidgetNotFoundException {

            //Arrange
            when(widgetService.getWidget(Mockito.any(UUID.class)))
                    .thenThrow(new WidgetNotFoundException());

            assertThatCode(() -> {
                //Act
                //Assert
                mockMvc.perform(get("/api/v1/widgets/" + UUID.randomUUID()))
                        .andDo(print())
                        .andExpect(status().isNotFound());
            }).doesNotThrowAnyException();
        }

        @Test
        public void should_return_400_code_when_pass_invalid_parameters() throws WidgetNotFoundException {

            //Arrange
            when(widgetService.getWidget(Mockito.any(UUID.class)))
                    .thenReturn(new WidgetDto());

            assertThatCode(() -> {
                //Act
                //Assert
                mockMvc.perform(get("/api/v1/widgets/89349idfgkow-fsdfsd"))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }).doesNotThrowAnyException();
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest
    @AutoConfigureMockMvc
    @Category(WidgetControllerTest.class)
    public static class UpdateWidget{

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private WidgetServiceImpl widgetService;

        @Test
        public void should_return_200_code_when_successful_update_widget(){
            assertThatCode(() -> {
                //Act
                //Assert
                mockMvc.perform(put("/api/v1/widgets/" + UUID.randomUUID())
                        .param("x", "1")
                        .param("y", "1")
                        .param("width","1")
                        .param("height", "1")
                        .param("zIndex", "1"))
                        .andDo(print())
                        .andExpect(status().isOk());
            }).doesNotThrowAnyException();
        }

        @Test
        public void should_return_404_code_when_pass_guid_and_widget_not_exist() throws WidgetNotFoundException {

            //Arrange
            doThrow(new WidgetNotFoundException()).when(widgetService).updateWidget(Mockito.any(UUID.class), Mockito.any(WidgetLayoutInfo.class));

            assertThatCode(() -> {
                //Act
                //Assert
                mockMvc.perform(put("/api/v1/widgets/" + UUID.randomUUID())
                        .param("x", "1")
                        .param("y", "1")
                        .param("width","1")
                        .param("height", "1")
                        .param("zIndex", "1"))
                        .andDo(print())
                        .andExpect(status().isNotFound());
            }).doesNotThrowAnyException();
        }

        @Test
        public void should_return_400_code_when_pass_invalid_parameters() throws WidgetNotFoundException {
            assertThatCode(() -> {
                //Act
                //Assert
                mockMvc.perform(put("/api/v1/widgets/" + UUID.randomUUID())
                        .param("x", "-1")
                        .param("y", "-1")
                        .param("width","-1")
                        .param("height", "-1")
                        .param("zIndex", "-1"))
                        .andDo(print())
                        .andExpect(status().isBadRequest());
            }).doesNotThrowAnyException();
        }
    }


}
