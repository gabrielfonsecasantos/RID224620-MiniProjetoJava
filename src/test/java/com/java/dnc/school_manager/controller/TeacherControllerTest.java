package com.java.dnc.school_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.dnc.school_manager.dto.TeacherDTO;
import com.java.dnc.school_manager.exception.DuplicateCpfException;
import com.java.dnc.school_manager.exception.ResourceNotFoundException;
import com.java.dnc.school_manager.model.Address;
import com.java.dnc.school_manager.model.Teacher;
import com.java.dnc.school_manager.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherController.class)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeacherService teacherService;

    private Teacher teacher;
    private TeacherDTO teacherDTO;

    @BeforeEach
    void setUp() {
        Address address = new Address();
        address.setCep("04538133");
        address.setStreet("Rua Funchal");
        address.setNumber("200");
        address.setNeighborhood("Vila Olimpia");
        address.setCity("Sao Paulo");
        address.setUf("SP");

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Maria Santos");
        teacher.setCpf("98765432100");
        teacher.setEmail("maria@email.com");
        teacher.setPhoneNumber("11888888888");
        teacher.setSubject("Mathematics");
        teacher.setAddress(address);

        teacherDTO = new TeacherDTO();
        teacherDTO.setName("Maria Santos");
        teacherDTO.setCpf("98765432100");
        teacherDTO.setEmail("maria@email.com");
        teacherDTO.setPhoneNumber("11888888888");
        teacherDTO.setSubject("Mathematics");
        teacherDTO.setCep("04538133");
        teacherDTO.setNumber("200");
    }

    @Test
    @DisplayName("GET /api/teachers - Should return all teachers")
    void findAll_ShouldReturnAllTeachers() throws Exception {
        when(teacherService.findAll()).thenReturn(Arrays.asList(teacher));

        mockMvc.perform(get("/api/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Maria Santos"));
    }

    @Test
    @DisplayName("GET /api/teachers/{id} - Should return teacher")
    void findById_ShouldReturnTeacher() throws Exception {
        when(teacherService.findById(1L)).thenReturn(teacher);

        mockMvc.perform(get("/api/teachers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Santos"))
                .andExpect(jsonPath("$.subject").value("Mathematics"));
    }

    @Test
    @DisplayName("GET /api/teachers/{id} - Should return 404 when not found")
    void findById_ShouldReturn404_WhenNotFound() throws Exception {
        when(teacherService.findById(99L)).thenThrow(new ResourceNotFoundException("Teacher not found"));

        mockMvc.perform(get("/api/teachers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/teachers - Should create teacher")
    void create_ShouldCreateTeacher() throws Exception {
        when(teacherService.create(any(TeacherDTO.class))).thenReturn(teacher);

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maria Santos"));
    }

    @Test
    @DisplayName("POST /api/teachers - Should return 400 when CPF exists")
    void create_ShouldReturn400_WhenCpfExists() throws Exception {
        when(teacherService.create(any(TeacherDTO.class)))
                .thenThrow(new DuplicateCpfException("CPF already registered"));

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/teachers/{id} - Should delete teacher")
    void delete_ShouldDeleteTeacher() throws Exception {
        mockMvc.perform(delete("/api/teachers/1"))
                .andExpect(status().isNoContent());
    }
}
