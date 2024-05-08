package com.ead.authuser.dtos;

import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.ead.authuser.validation.UsernameConstraint;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)// para ocutar os dados null, também tem outras opcoes, não somente para null
public class UserDto {
	// quando usa o spring validation você deve também inserir na controller 
	//Uso do json view é para delimitar quais os atributos será mostrado ao passar o DTO como parametro (em anotações de validação também)
	// primeiro deve ser criado uma interface com os métodos 
	// depois incluir a notação @JsonView para delimitar quais são os métodos que terão a visualização
	// depois deve ser incluido na controller como parametro para saber qual o método
	//criando um notaçao UsernameConstraint
	//Entrada de dados via post ou update
	
	public interface UserView { //diferentes visoes de usuarios para cada alteração
		public static interface RegistrationPost {} // Cadastro do usuario
		public static interface UserPut {} //atualizar dados basicos 
		public static interface passwordPut {} //atualizar senha 
		public static interface imagePut {} // atualizar imagem 
	}
    
	private UUID userId;
	
	@NotBlank(groups = UserView.RegistrationPost.class) // atilizando validation. não permite valor null ou vazio. 
	//Quando está sendo utilizado o json view deve ser adicionado a notação do validation qual o grupo que ele deve ser aplicado. groups =
	@Size(min = 4, max = 50, groups = UserView.RegistrationPost.class) // tamanho min e max com o validation
	@UsernameConstraint(groups = UserView.RegistrationPost.class) // notação criada 
	@JsonView(UserView.RegistrationPost.class)
	private String username;
	
	@NotBlank(groups = UserView.RegistrationPost.class)
	@Email // spring validation
	@Size(min = 4, max = 50, groups = UserView.RegistrationPost.class)
	@JsonView(UserView.RegistrationPost.class)
	private String email;
	
	@NotBlank(groups = {UserView.RegistrationPost.class, UserView.passwordPut.class}) // para mais de uma visão deve colocar o {} e , 
	@JsonView({UserView.RegistrationPost.class, UserView.passwordPut.class})
	@Size(min = 6, max = 20, groups = {UserView.RegistrationPost.class, UserView.passwordPut.class})
	private String password;
	
	@NotBlank(groups = UserView.passwordPut.class)
	@JsonView(UserView.passwordPut.class)
	@Size(min = 6, max = 20, groups = UserView.passwordPut.class)
	private String oldPassword;
	
	@JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
	private String fullName;
	
	@JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
	private String phoneNumber;
	
	@JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
	private String cpf;
	
	@NotBlank(groups = UserView.imagePut.class)
	@JsonView({UserView.imagePut.class})
	private String imageUrl;

		
}
