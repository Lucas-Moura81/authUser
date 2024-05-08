package com.ead.authuser.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.RepresentationModel;

import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

//serializable conversão de onb para sequencia de byte para mepear no banco de dados
// adicionando hateoas para hiperlink, primeiro deve extender a classe RepresentationModel e a entidade <> 
// depois de adicionar o heteoas gerou um erro no data, @EqualsAndHashCode(callSuper = false) incluido para tirar o warning. 
// depois de adicionado deve ir a controller no caso. 
@Entity
@Table(name = "TB_USERS")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)// para ocutar os dados null, também tem outras opcoes, não somente para null
public class UserModel extends RepresentationModel<UserModel> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)// SERÃO ID UNICOS IND DA APLICACAO QUE ESTEJA USANDO
    private UUID userId;
    // null, unico, tamanho 
    @Column(nullable = false, unique = true, length = 50) //PARAMETROS PARA A COLUNA NO BANCO
    private String username;
    
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    
    @Column(nullable = false, length = 255)
    @JsonIgnore     // Para não mostrar no json (não pode serializar)
    private String password;
    
    @Column(nullable = false, length = 150)
    private String fullName;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING) //PARA TRANSFORMAR O ENUM COMO UMA STRING NO BANCO DE DADOS
    private UserStatus userStatus;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 20)
    private String cpf;
    
    @Column
    private String imageUrl;
    
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    //shape (formato esperado) padrão que quer retorna a data. tem outros shape)
    private LocalDateTime creationDate;
    
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastUpdateDate;
    
  
    //relacão bidirecional. relacionamento com tabela userCourseModel classe para criar tabela. 
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserCourseModel> usersCourses; 
    
    
    
    // MÉTODO DE CONVERSÃO + UMA OPÇÃO ALÉM DO BEANSUTIL
    public UserCourseModel convertToUserCourseModel(UUID courseId) {
    	// id gera att, this pq ja estamos em curse model. user id que vem como param do método. tem que adicionar os @Construtores com e sem args
    	return new UserCourseModel(null, courseId, this);
    }
	

}
