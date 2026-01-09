package com.java.dnc.school_manager.service;

import com.java.dnc.school_manager.dto.StudentDTO;
import com.java.dnc.school_manager.dto.ViaCepResponse;
import com.java.dnc.school_manager.exception.DuplicateCpfException;
import com.java.dnc.school_manager.exception.InvalidCepException;
import com.java.dnc.school_manager.exception.ResourceNotFoundException;
import com.java.dnc.school_manager.model.Address;
import com.java.dnc.school_manager.model.Student;
import com.java.dnc.school_manager.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final ViaCepService viaCepService;

    public StudentService(StudentRepository studentRepository, ViaCepService viaCepService) {
        this.studentRepository = studentRepository;
        this.viaCepService = viaCepService;
    }

    // List all students
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    // Find by id
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    // Create Student
    public Student create(StudentDTO dto) {
        // CPF verification
        if (studentRepository.existsByCpf(dto.getCpf())) {
            throw new DuplicateCpfException("CPF already registered: " + dto.getCpf());
        }
        // Search CEP with ViaCEP
        ViaCepResponse viaCep = viaCepService.fetchAddress(dto.getCep());
        if (viaCep.getError() != null) {
            throw new InvalidCepException("Invalid CEP: " + dto.getCep());
        }
        // Converts DTO to Entity
        Student student = new Student();
        mapToEntity(dto, student, viaCep);
        return studentRepository.save(student);
    }

    // Update Student
    public Student update(Long id, StudentDTO dto) {
        Student student = findById(id);

        // Check if new CPF already exists in another record
        if (!student.getCpf().equals(dto.getCpf()) && studentRepository.existsByCpf(dto.getCpf())) {
            throw new DuplicateCpfException("CPF already registered: " + dto.getCpf());
        }

        ViaCepResponse viaCep = viaCepService.fetchAddress(dto.getCep());
        if (viaCep.getError() != null) {
            throw new InvalidCepException("Invalid CEP: " + dto.getCep());
        }

        mapToEntity(dto, student, viaCep);
        return studentRepository.save(student);
    }

    // Delete a Student
    public void delete(Long id) {
        Student student = findById(id);
        studentRepository.delete(student);
    }

    private void mapToEntity(StudentDTO dto, Student student, ViaCepResponse viaCep) {
        student.setName(dto.getName());
        student.setCpf(dto.getCpf());
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setRegistration(dto.getRegistration());
        student.setRegistrationDate(dto.getRegistrationDate());

        Address address = new Address();
        address.setCep(dto.getCep());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setStreet(dto.getStreet() != null ? dto.getStreet() : viaCep.getStreet());
        address.setNeighborhood(dto.getNeighborhood() != null ? dto.getNeighborhood() : viaCep.getNeighborhood());
        address.setCity(dto.getCity() != null ? dto.getCity() : viaCep.getCity());
        address.setUf(dto.getUf() != null ? dto.getUf() : viaCep.getUf());

        student.setAddress(address);
    }
}
