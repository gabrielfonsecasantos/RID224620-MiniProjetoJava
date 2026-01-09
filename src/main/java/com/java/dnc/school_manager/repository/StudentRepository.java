package com.java.dnc.school_manager.repository;

import com.java.dnc.school_manager.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByCpf(String cpf);
    boolean existsByCpf(String cpf);

    void delete(Optional<Student> student);
}
