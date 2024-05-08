package com.ead.authuser.models;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)// para ocutar os dados null, também tem outras opcoes, não somente para null
@Table(name = "TB_USERS_COURSES")
// criando uma tabela para relação de user com curse para comunicação entre os microserviços 
// mapeamento bidirecional 
public class UserCourseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id; 
	
	@Column(nullable = false)
	private UUID courseId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private UserModel user;
}
