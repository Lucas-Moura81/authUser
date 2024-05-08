package com.ead.authuser.client;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.ResponsePageDto;
import com.ead.authuser.services.UtilsService;

import lombok.extern.log4j.Log4j2;

// classe de conf --> RestTemplateConfig
@Component
@Log4j2
public class CourseClient {

	// requisição esterna para course 
	@Autowired
	RestTemplate restTemplate; 
	// importe import org.springframework.web.client.RestTemplate;
	
	@Autowired
	UtilsService utilsService; 
	
	@Value("${ead.api.url.course}")
	String REQUEST_URL_COURSE;
	
    public Page<CourseDto> getAllCoursesByUser(UUID userId, Pageable pageable){
        List<CourseDto> searchResult = null;
        ResponseEntity<ResponsePageDto<CourseDto>> result = null;
        String url = REQUEST_URL_COURSE + utilsService.createUrl(userId, pageable);
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);
        try{
            ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<CourseDto>>() {};
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();
            log.debug("Response Number of Elements: {} ", searchResult.size());
        } catch (HttpStatusCodeException e){
            log.error("Error request /courses {} ", e);
        }
        log.info("Ending request /courses userId {} ", userId);
        return result.getBody();
    }

	public void deleteUserInCourse(UUID userId) {
	// montando a url do outro servico
	String url = REQUEST_URL_COURSE + "/course/users/" + userId;
	// montando o restTamplate para o outro serviço 
	restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
	
	// agora tem que criar no outro para receber.
		
	}
}
