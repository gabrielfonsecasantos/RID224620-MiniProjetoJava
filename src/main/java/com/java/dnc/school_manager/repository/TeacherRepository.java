package com.java.dnc.school_manager.repository;

import com.java.dnc.school_manager.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
