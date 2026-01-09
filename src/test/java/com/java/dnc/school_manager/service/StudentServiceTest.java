package com.java.dnc.school_manager.service;

import com.java.dnc.school_manager.dto.StudentDTO;
import com.java.dnc.school_manager.dto.ViaCepResponse;
import com.java.dnc.school_manager.exception.DuplicateCpfException;
import com.java.dnc.school_manager.exception.InvalidCepException;
import com.java.dnc.school_manager.exception.ResourceNotFoundException;
import com.java.dnc.school_manager.model.Student;
import com.java.dnc.school_manager.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ViaCepService viaCepService;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private StudentDTO studentDTO;
    private ViaCepResponse viaCepResponse;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setName("Joao Silva");
        student.setCpf("12345678900");
        student.setEmail("joao@email.com");
        student.setPhoneNumber("11999999999");

        studentDTO = new StudentDTO();
        studentDTO.setName("Joao Silva");
        studentDTO.setCpf("12345678900");
        studentDTO.setEmail("joao@email.com");
        studentDTO.setPhoneNumber("11999999999");
        studentDTO.setCep("01310100");
        studentDTO.setNumber("100");

        viaCepResponse = new ViaCepResponse();
        viaCepResponse.setCep("01310100");
        viaCepResponse.setStreet("Avenida Paulista");
        viaCepResponse.setNeighborhood("Bela Vista");
        viaCepResponse.setCity("Sao Paulo");
        viaCepResponse.setUf("SP");
    }

    @Test
    @DisplayName("Should return all students")
    void findAll_ShouldReturnAllStudents() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student));

        List<Student> result = studentService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return student by ID")
    void findById_ShouldReturnStudent_WhenIdExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student result = studentService.findById(1L);

        assertNotNull(result);
        assertEquals("Joao Silva", result.getName());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.findById(99L));
        verify(studentRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should create student successfully")
    void create_ShouldCreateStudent_WhenDataIsValid() {
        when(studentRepository.existsByCpf(anyString())).thenReturn(false);
        when(viaCepService.fetchAddress(anyString())).thenReturn(viaCepResponse);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student result = studentService.create(studentDTO);

        assertNotNull(result);
        verify(studentRepository, times(1)).existsByCpf(studentDTO.getCpf());
        verify(viaCepService, times(1)).fetchAddress(studentDTO.getCep());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when CPF already exists")
    void create_ShouldThrowException_WhenCpfExists() {
        when(studentRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(DuplicateCpfException.class, () -> studentService.create(studentDTO));
        verify(studentRepository, times(1)).existsByCpf(studentDTO.getCpf());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when CEP is invalid")
    void create_ShouldThrowException_WhenCepIsInvalid() {
        ViaCepResponse invalidCep = new ViaCepResponse();
        invalidCep.setError("true");

        when(studentRepository.existsByCpf(anyString())).thenReturn(false);
        when(viaCepService.fetchAddress(anyString())).thenReturn(invalidCep);

        assertThrows(InvalidCepException.class, () -> studentService.create(studentDTO));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should update student successfully")
    void update_ShouldUpdateStudent_WhenDataIsValid() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(viaCepService.fetchAddress(anyString())).thenReturn(viaCepResponse);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student result = studentService.update(1L, studentDTO);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should delete student successfully")
    void delete_ShouldDeleteStudent_WhenIdExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).delete(student);

        assertDoesNotThrow(() -> studentService.delete(1L));
        verify(studentRepository, times(1)).delete(student);
    }
}
