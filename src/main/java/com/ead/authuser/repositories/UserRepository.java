package com.ead.authuser.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ead.authuser.models.UserModel;
// incluindo biblioteca do spec executer e passando a entidade

public interface UserRepository extends JpaRepository<UserModel, UUID>,JpaSpecificationExecutor<UserModel>{
	
	
	boolean existsByUsername(String username);
	
	boolean existsByEmail(String email);


}
