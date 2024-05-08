package com.ead.authuser.controllers;

//importe para os heteos 
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.especifications.EspecificationsTemplate;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600) // permite acesso em todas as origens. pode utilizar por métodos 
@RequestMapping("/users") // 1 nivel de maturidade 
// implementação de consultas pág. utlizando do spring data 
@Log4j2
public class UserController {

	@Autowired
	UserService userService;
	
	// paginação para o método getall
	// método que retorna uma lista de todos usuarios. 
	//definir PageableDefoul, page começa em 0, size quantidade de elemento por pág, sort qual será a ordenação do menor para o maior direction para o sort. 
	//receber um Pageable do spring data (Atenção a importação)
	// depois de adicionado os parametros na controller, deve criar o método na service. método findAll com parametro que faz a busca** atenção a isso pq pode ser criado na repository se não tiver pelo JPA. 
	// adicionando o especification EspecificationsTemplate
	// implementando o heteos. 
	@GetMapping
	public ResponseEntity<Page<UserModel>> getAllUsers(EspecificationsTemplate.UserSpec spec,
			@PageableDefault(page = 0, size = 10, sort = "userId", direction = Direction.ASC) Pageable pageable,
	        @RequestParam(required = false) UUID courseId
	    ){
		// passando o spec como parametro para o método. depois deve adicionar na service. 
		Page<UserModel> userModelPage = null; 
		if(courseId != null) {
			userModelPage = userService.findAll(EspecificationsTemplate.UserCourseId(courseId).and(spec), pageable);
		} else {
			userModelPage = userService.findAll(spec, pageable);
		}
		
		// verificação da paginação para implementar o heteos
		if(!userModelPage.isEmpty()) { // se ela não for vazia vai entrar no recurso 
			for(UserModel user : userModelPage.toList()) {// to list para acessar cada elemento
				
				//depois de extender a classe na entidade  vc pode acessar o add
				// as vezes a ide não reconhece os imports para o metodos**
				// add -> representetion mode
				// linkTo mapeia a calsse; heteos - passa o controler e o método que pertence a controler que vai usar, passou o id pq recebe como param um id
				//methoOn mapeaia a função
				//withSelfRel qual a relação com o link para aquele recurso
				
				user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
			}
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(userModelPage); 
	}
	
	// método que busca um usuário por id atráves do get e usando como parametro um UUID. 
	// tipo de retorno obj pq pode retorno o user ou uma mensagem que não foi encontrato
	// como está sendo usado um optional deve ser usado o .get. 
	@GetMapping("/{userId}") // dentro da chaves o parametro para realizar a buscar. 
	public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId){
		Optional<UserModel> userModelOptional = userService.findById(userId); 
		if(!userModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado"); 
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
		}
	}
	
	//método que deleta usuários no banco 
	// usando um object e validação do obj. 
	@DeleteMapping("/{userId}")
	public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") UUID userId) {
		log.debug("DELETE deleteUser userId received {} ", userId);
		Optional<UserModel> userModelOptional = userService.findById(userId);
		if (!userModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
		} else
			userService.delete(userModelOptional.get());
		    log.debug("DELETE deleteUser userId deleted {} ", userId);
            log.info("User deleted successfully userId {} ", userId);
		    return ResponseEntity.status(HttpStatus.OK).body("Usuário deletado com sucesso");

	}
	
	// método que altera alguns atributos de userModel, utilizando da interface userView. 
	// Método para atualização do cadastro, validação e utilizando o jsonView 
	//@Validated(UserDto.UserView.UserPut.class) implementando a validação feita no DTO. depois de incerir as anotações deve ser adicionado aqui logo depois do body. 
	@PutMapping("/{userId}")
	public ResponseEntity<Object> upDateUser(@PathVariable(value = "userId") UUID userId, 
			                                 @RequestBody @Validated(UserDto.UserView.UserPut.class) 
	                                         @JsonView(UserDto.UserView.UserPut.class)UserDto userDto) {
		log.debug("PUT updateUser userDto received {} ", userDto.toString());
		Optional<UserModel> userModelOptional = userService.findById(userId);
		UserModel userModel = userModelOptional.get();
		if (!userModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
		} else
			userModel.setFullName(userDto.getFullName());
		    userModel.setCpf(userDto.getCpf()); 
		    userModel.setPhoneNumber(userDto.getPhoneNumber());
		    userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
		    userService.save(userModel); 
            log.debug("PUT updateUser userId saved {} ", userModel.getUserId());
            log.info("User updated successfully userId {} ", userModel.getUserId());
		    return ResponseEntity.status(HttpStatus.OK).body(userModel);

	}
	
	
	// Método para atualização da senha, validação e utilizando o jsonView 
	@PutMapping("/{userId}/password")
	public ResponseEntity<Object> upDatePassword(@PathVariable(value = "userId") UUID userId,
			                                     @RequestBody @Validated(UserDto.UserView.passwordPut.class)                         
	                                             @JsonView(UserDto.UserView.passwordPut.class) UserDto userDto) {
		log.debug("PUT updatePassword userDto received {} ", userDto.toString());
		Optional<UserModel> userModelOptional = userService.findById(userId);
		UserModel userModel = userModelOptional.get();
		if (!userModelOptional.isPresent()) {
			 log.warn("Mismatched old password userId {} ", userId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
		}
		if (!userModelOptional.get().getPassword().equals(userDto.getOldPassword())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Senha atual igual a anterior");
		} else {
			userModel.setPassword(userDto.getPassword());
			userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
			userService.save(userModel);
			log.debug("PUT updatePassword UserId saved {} ", userModel.getUserId());
            log.info("Password updated successfully userId {} ", userModel.getUserId());
			return ResponseEntity.status(HttpStatus.OK).body("Senha alterada com sucesso!");

		}
	}
	// Método para atualização da imagem utilizando  validação e utilizando o jsonView 
	@PutMapping("/{userId}/image")
	public ResponseEntity<Object> upDateImage(@PathVariable(value = "userId") UUID userId,
			                                  @RequestBody @Validated(UserDto.UserView.imagePut.class)                      
			                                  @JsonView(UserDto.UserView.imagePut.class) UserDto userDto) {
		log.debug("PUT updateImage userDto received {} ", userDto.toString());
		Optional<UserModel> userModelOptional = userService.findById(userId);
		UserModel userModel = userModelOptional.get();
		if (!userModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
		}
		else {
			userModel.setImageUrl(userDto.getImageUrl());
			userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
			userService.save(userModel);
            log.debug("PUT updateImage UserId saved {} ", userModel.getUserId());
            log.info("Image updated successfully userId {} ", userModel.getUserId());
			return ResponseEntity.status(HttpStatus.OK).body(userModel);

		}
	}	
}