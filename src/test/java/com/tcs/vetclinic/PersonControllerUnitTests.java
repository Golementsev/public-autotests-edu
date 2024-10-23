package com.tcs.vetclinic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.vetclinic.domain.person.Person;
import com.tcs.vetclinic.service.PersonNotExistError;
import com.tcs.vetclinic.service.PersonNotFoundError;
import com.tcs.vetclinic.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
public class PersonControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService stubPersonService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание клиента - успешный сценарий")
    public void shouldCreatePersonSuccessfully() throws Exception {
        // Создаем объект Person без ID
        Person person = new Person(null, "John Doe");

        // Создаем объект Person с ID для возврата
        Person createdPerson = new Person(1L, "John Doe");

        // Настраиваем заглушку для сервиса
        when(stubPersonService.save(any(Person.class))).thenReturn(createdPerson);

        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(createdPerson.getId())); // Проверяем, что возвращается ID созданного клиента
    }



    @Test
    @DisplayName("Создание клиента - ошибка при существующем id")
    public void shouldGet500WhenCreateOfExistingId() throws Exception {
        Person person = new Person(1L, "John Doe");
        when(stubPersonService.save(any(Person.class))).thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Получение клиента по id - успешный сценарий")
    public void shouldGetPersonByIdSuccessfully() throws Exception {
        Person person = new Person(1L, "John Doe");
        when(stubPersonService.findById(anyLong())).thenReturn(person);

        mockMvc.perform(MockMvcRequestBuilders.get("/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @DisplayName("Получение клиента по id - клиент не найден")
    public void shouldGet404WhenFindByIdOfNotExisted() throws Exception {
        when(stubPersonService.findById(anyLong())).thenThrow(new PersonNotFoundError("Клиент не найден"));

        mockMvc.perform(MockMvcRequestBuilders.get("/person/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удаление клиента - клиент не существует")
    public void shouldGet409WhenDeleteOfNotExistingId() throws Exception {
        doThrow(new PersonNotExistError("Клиент не найден")).when(stubPersonService).deleteById(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/person/1"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Обновление клиента - успешный сценарий")
    public void shouldUpdatePersonSuccessfully() throws Exception {
        Person person = new Person(1L, "John Doe Updated");

        mockMvc.perform(MockMvcRequestBuilders.put("/person/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Удаление клиента - успешный сценарий")
    public void shouldDeletePersonSuccessfully() throws Exception {
        doNothing().when(stubPersonService).deleteById(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/person/1"))
                .andExpect(status().isNoContent());
    }
}
