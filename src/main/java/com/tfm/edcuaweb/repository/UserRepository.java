package com.tfm.edcuaweb.repository;

import java.util.Optional;


import com.tfm.edcuaweb.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfm.edcuaweb.model.User;
@Repository
public interface UserRepository extends JpaRepository <User, Long>{

	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
    Optional<User> findById(Long userId);
	boolean existsByEmail (String email);
	boolean existsByUsername(String username);
    long countByRole(Role role);
	
	
}
