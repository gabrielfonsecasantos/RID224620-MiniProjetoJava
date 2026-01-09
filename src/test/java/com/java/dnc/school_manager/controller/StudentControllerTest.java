package com.java.dnc.school_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.dnc.school_manager.dto.StudentDTO;
import com.java.dnc.school_manager.exception.DuplicateCpfException;
import com.java.dnc.school_manager.exception.ResourceNotFoundException;
import com.java.dnc.school_manager.model.Address;
import com.java.dnc.school_manager.model.Student;
import com.java.dnc.school_manager.service.StudentService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    private Student student;
    private StudentDTO studentDTO;

    @BeforeEach
    void setUp() {
        Address address = new Address();
        address.setCep("01310100");
        address.setStreet("Avenida Paulista");
        address.setNumber("100");
        address.setNeighborhood("Bela Vista");
        address.setCity("Sao Paulo");
        address.setUf("SP");

        student = new Student();
        student.setId(1L);
        student.setName("Joao Silva");
        student.setCpf("12345678900");
        student.setEmail("joao@email.com");
        student.setPhoneNumber("11999999999");
        student.setAddress(address);

        studentDTO = new StudentDTO();
        studentDTO.setName("Joao Silva");
        studentDTO.setCpf("12345678900");
        studentDTO.setEmail("joao@email.com");
        studentDTO.setPhoneNumber("11999999999");
        studentDTO.setCep("01310100");
        studentDTO.setNumber("100");
    }

    @Test
    @DisplayName("GET /api/students - Should return all students")
    void findAll_ShouldReturnAllStudents() throws Exception {
        when(studentService.findAll()).thenReturn(Arrays.asList(student));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Joao Silva"));
    }

    @Test
    @DisplayName("GET /api/students/{id} - Should return student")
    void findById_ShouldReturnStudent() throws Exception {
        when(studentService.findById(1L)).thenReturn(student);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joao Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678900"));
    }

    @Test
    @DisplayName("GET /api/students/{id} - Should return 404 when not found")
    void findById_ShouldReturn404_WhenNotFound() throws Exception {
        when(studentService.findById(99L)).thenThrow(new ResourceNotFoundException("Student not found"));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/students - Should create student")
    void create_ShouldCreateStudent() throws Exception {
        when(studentService.create(any(StudentDTO.class))).thenReturn(student);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Joao Silva"));
    }

    @Test
    @DisplayName("POST /api/students - Should return 400 when CPF exists")
    void create_ShouldReturn400_WhenCpfExists() throws Exception {
        when(studentService.create(any(StudentDTO.class)))
                .thenThrow(new DuplicateCpfException("CPF already registered"));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/students/{id} - Should update student")
    void update_ShouldUpdateStudent() throws Exception {
        when(studentService.update(anyLong(), any(StudentDTO.class))).thenReturn(student);

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joao Silva"));
    }

    @Test
    @DisplayName("DELETE /api/students/{id} - Should delete student")
    void delete_ShouldDeleteStudent() throws Exception {
        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());
    }
}
