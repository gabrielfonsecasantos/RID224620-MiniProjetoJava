package com.java.dnc.school_manager.service;

import com.java.dnc.school_manager.dto.TeacherDTO;
import com.java.dnc.school_manager.dto.ViaCepResponse;
import com.java.dnc.school_manager.exception.DuplicateCpfException;
import com.java.dnc.school_manager.exception.InvalidCepException;
import com.java.dnc.school_manager.exception.ResourceNotFoundException;
import com.java.dnc.school_manager.model.Teacher;
import com.java.dnc.school_manager.repository.TeacherRepository;
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
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ViaCepService viaCepService;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;
    private TeacherDTO teacherDTO;
    private ViaCepResponse viaCepResponse;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Maria Santos");
        teacher.setCpf("98765432100");
        teacher.setEmail("maria@email.com");
        teacher.setPhoneNumber("11888888888");
        teacher.setSubject("Mathematics");

        teacherDTO = new TeacherDTO();
        teacherDTO.setName("Maria Santos");
        teacherDTO.setCpf("98765432100");
        teacherDTO.setEmail("maria@email.com");
        teacherDTO.setPhoneNumber("11888888888");
        teacherDTO.setSubject("Mathematics");
        teacherDTO.setCep("04538133");
        teacherDTO.setNumber("200");

        viaCepResponse = new ViaCepResponse();
        viaCepResponse.setCep("04538133");
        viaCepResponse.setStreet("Rua Funchal");
        viaCepResponse.setNeighborhood("Vila Olimpia");
        viaCepResponse.setCity("Sao Paulo");
        viaCepResponse.setUf("SP");
    }

    @Test
    @DisplayName("Should return all teachers")
    void findAll_ShouldReturnAllTeachers() {
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher));

        List<Teacher> result = teacherService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return teacher by ID")
    void findById_ShouldReturnTeacher_WhenIdExists() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findById(1L);

        assertNotNull(result);
        assertEquals("Maria Santos", result.getName());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when teacher not found")
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> teacherService.findById(99L));
        verify(teacherRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should create teacher successfully")
    void create_ShouldCreateTeacher_WhenDataIsValid() {
        when(teacherRepository.existsByCpf(anyString())).thenReturn(false);
        when(viaCepService.fetchAddress(anyString())).thenReturn(viaCepResponse);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        Teacher result = teacherService.create(teacherDTO);

        assertNotNull(result);
        verify(teacherRepository, times(1)).existsByCpf(teacherDTO.getCpf());
        verify(viaCepService, times(1)).fetchAddress(teacherDTO.getCep());
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should throw exception when CPF already exists")
    void create_ShouldThrowException_WhenCpfExists() {
        when(teacherRepository.existsByCpf(anyString())).thenReturn(true);

        assertThrows(DuplicateCpfException.class, () -> teacherService.create(teacherDTO));
        verify(teacherRepository, times(1)).existsByCpf(teacherDTO.getCpf());
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should throw exception when CEP is invalid")
    void create_ShouldThrowException_WhenCepIsInvalid() {
        ViaCepResponse invalidCep = new ViaCepResponse();
        invalidCep.setError("true");

        when(teacherRepository.existsByCpf(anyString())).thenReturn(false);
        when(viaCepService.fetchAddress(anyString())).thenReturn(invalidCep);

        assertThrows(InvalidCepException.class, () -> teacherService.create(teacherDTO));
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should delete teacher successfully")
    void delete_ShouldDeleteTeacher_WhenIdExists() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        doNothing().when(teacherRepository).delete(teacher);

        assertDoesNotThrow(() -> teacherService.delete(1L));
        verify(teacherRepository, times(1)).delete(teacher);
    }
}
