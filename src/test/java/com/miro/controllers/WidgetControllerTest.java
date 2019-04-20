package com.miro.controllers;

import com.miro.core.dto.WidgetDto;
import com.miro.services.widgetManager.WidgetServiceImpl;
import com.miro.services.widgetManager.WidgetServiceImplTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.theories.DataPoints;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(WidgetControllerTest.class)
@Suite.SuiteClasses({ WidgetControllerTest.CreateWidget.class})
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
}
