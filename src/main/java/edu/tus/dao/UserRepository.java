package edu.tus.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.tus.model.StudentEntity;

public interface UserRepository extends JpaRepository<StudentEntity, Long> {

	Optional<StudentEntity> findByUsername(String username);

}

