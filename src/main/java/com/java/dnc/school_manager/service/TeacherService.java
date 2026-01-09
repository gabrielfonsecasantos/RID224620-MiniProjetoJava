package com.java.dnc.school_manager.service;

import com.java.dnc.school_manager.dto.TeacherDTO;
import com.java.dnc.school_manager.dto.ViaCepResponse;
import com.java.dnc.school_manager.exception.DuplicateCpfException;
import com.java.dnc.school_manager.exception.InvalidCepException;
import com.java.dnc.school_manager.exception.ResourceNotFoundException;
import com.java.dnc.school_manager.model.Address;
import com.java.dnc.school_manager.model.Student;
import com.java.dnc.school_manager.model.Teacher;
import com.java.dnc.school_manager.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    private TeacherRepository teacherRepository;
    private ViaCepService viaCepService;

    //List all teachers
    public List<Teacher> findAll(){
        return teacherRepository.findAll();
    }

    //Find by id
    public Teacher findById(Long id){
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    //Create Teacher
    public Teacher create(TeacherDTO dto){
        //CPF verification
        if(teacherRepository.existsByCpf(dto.getCpf())){
            throw new DuplicateCpfException("CPF already registered: " + dto.getCpf());
        }

        //Search CEP with ViaCEP
        ViaCepResponse viaCep = viaCepService.fetchAddress(dto.getCep());
        if(viaCep.getError() != null){
            throw new InvalidCepException("Invalid CEP: " + dto.getCep());
        }

        //Converts DTO to Entity
        Teacher teacher = new Teacher();
        mapToEntity(dto, teacher, viaCep);
        return teacherRepository.save(teacher);
    }

        //Update teacher
        public Teacher update(Long id, TeacherDTO dto){
            Teacher teacher = findById(id);

            // Check if new CPF already exists in another record
            if(!teacher.getCpf().equals(dto.getCpf()) && teacherRepository.existsByCpf(dto.getCpf())){
                throw new DuplicateCpfException("CPF already registered: " + dto.getCpf());
            }

            ViaCepResponse viaCep = viaCepService.fetchAddress(dto.getCep());
            if(viaCep.getError() != null){
                throw new InvalidCepException("Invalid CEP: " + dto.getCep());
            }

            mapToEntity(dto, teacher, viaCep);
            return teacherRepository.save(teacher);
        }

        //Delete teacher
        public void delete(Long id){
            Teacher teacher = findById(id);
            teacherRepository.delete(teacher);
        }

    private void mapToEntity(TeacherDTO dto, Teacher teacher, ViaCepResponse viaCep) {
        teacher.setName(dto.getName());
        teacher.setCpf(dto.getCpf());
        teacher.setEmail(dto.getEmail());
        teacher.setPhoneNumber(dto.getPhoneNumber());
        teacher.setSubject(dto.getSubject());
        teacher.setHiringDate(dto.getHiringDate());

        Address address = new Address();
        address.setCep(dto.getCep());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setStreet(dto.getStreet() != null ? dto.getStreet() : viaCep.getStreet());
        address.setNeighborhood(dto.getNeighborhood() != null ? dto.getNeighborhood() : viaCep.getNeighborhood());
        address.setCity(dto.getCity() != null ? dto.getCity() : viaCep.getCity());
        address.setUf(dto.getUf() != null ? dto.getUf() : viaCep.getUf());

        teacher.setAddress(address);
    }

}
