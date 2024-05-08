package com.ead.authuser.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600) // permite acesso em todas as origens. pode utilizar por métodos 
@RequestMapping("/auth") // 1 nivel de maturidade 
@Log4j2 //Usando logger. 
//utilizando loggin  sempre que for usar o log que n seja defauld tem que exlcuir o pacote de logg e adicionar o log4J2 no POM
public class AuthenticationController { 
	//Essa controller tem como objetivo criar um método que o usuário crie sua conta 
	//como um criar cadastro 
	// depois de criado a view com os métodos para o jsonView deve ser inserido como parametro na controller 
	// request body para parametros passados no corpo da requi. 
	
	
	@Autowired
	UserService userService; 
	
	@PostMapping("/signup")
	public ResponseEntity<Object> registerUser(@RequestBody 
			                                   @Validated(UserDto.UserView.RegistrationPost.class) 
			                                   @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto){
		// oque ta vindo em user dto 
	    log.debug("POST registerUser userDto {}", userDto.toString()); // { } vai ser substituido pelo que vem depois do ,   
		if(userService.existsByUsername(userDto.getUsername())) {
			log.warn("UserName {} está sendo usado ", userDto.getUsername()); 
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username já está sendo usado");
		} 
		if(userService.existsByEmail(userDto.getEmail())) {
			log.warn("Email {} está sendo usado ", userDto.getEmail()); 
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email já está sendo usado");
		}
		
		var userModel = new UserModel();
		//convertendo obj da entidade para DTO. 
		BeanUtils.copyProperties(userDto, userModel); 
		
		userModel.setUserStatus(UserStatus.ACTIVE);
		userModel.setUserType(UserType.STUDENT);
		userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
		userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
		
		userService.save(userModel); 
		log.debug("POST registerUser userId salvo {}", userModel.getUserId());
		log.info("Usuário salvo com sucesso {}", userModel.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(userModel); 
		
		
	}
	
	
}
