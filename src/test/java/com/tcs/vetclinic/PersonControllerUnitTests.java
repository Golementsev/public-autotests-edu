package com.tcs.vetclinic;

import com.tcs.vetclinic.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


public class PersonControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PersonService stubPersonService;
}

/*
     @Test
    public void shouldGet500WhenGetAllWithAnyError() throws Exception {
        when(stubPersonService.findAll(anyInt(), anyInt(), eq(SortType.ASC))).thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.get("/person")).andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldGet404WhenFindByIdOfNotExisted() throws Exception {
        when(stubPersonService.findById(1L)).thenThrow(new PersonNotFoundError("В репозитории нет клиента с таким id"));

        mockMvc.perform(MockMvcRequestBuilders.get("/person/1")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldGet500WhenCreateOfExistingId() throws Exception {
        when(stubPersonService.save(new Person(1L, "name"))).thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.post("/person")).andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldGet409WhenDeleteOfNotExistingId() throws Exception {
        doThrow(new PersonNotExistError("В репозитории нет клиента с таким id")).when(stubPersonService).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/person/1")).andExpect(status().isConflict());*/