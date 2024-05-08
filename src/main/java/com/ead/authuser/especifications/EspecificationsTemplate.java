package com.ead.authuser.especifications;

import java.util.UUID;

import javax.persistence.criteria.Join;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

// Verificar as notações 
// classe que implementa a classe de config do especification 

public class EspecificationsTemplate {
   // and para juntar todas as consultas.  Também pode usar o OR.
   // usar a notação spec, path é qual atributo ou enum vai ser usado 
  // spec = o tipo de condição .class 
	
	 @And({
         @Spec(path = "userType", spec = Equal.class),
         @Spec(path = "userStatus", spec = Equal.class),
         @Spec(path = "email", spec = Like.class),
         @Spec(path = "fullName", spec = Like.class)
 })
	// criar uma interface que extende Specification <Qual a entidade que contém os atributos para a consulta>
	public static interface UserSpec extends Specification<UserModel>{
	 }
	      
	// depois de criado aqui deve ser chamado no método da controller. 
		 
	public static Specification<UserModel> UserCourseId(final UUID courseId) {
		return (root, query, cb) -> {
			query.distinct(true);
			Join<UserModel, UserCourseModel> userProd = root.join("usersCourses");
			return cb.equal(userProd.get("courseId"), courseId);
		};
	}
		
	public static Specification<Page<CourseDto>> buscarCursoPorUsuario(final UUID userId) {
			return (root, query, cb) -> {
				query.distinct(true);
				Join<CourseDto, UserCourseModel> courseProd = root.join("usersCourses");
				return cb.equal(courseProd.get("userId"), userId);
			};

	}
 }

