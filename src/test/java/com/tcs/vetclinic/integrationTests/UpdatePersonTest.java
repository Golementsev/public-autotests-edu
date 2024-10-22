package com.tcs.vetclinic.integrationTests;

import com.tcs.vetclinic.domain.person.Person;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static io.qameta.allure.Allure.step;

public class UpdatePersonTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    public void putTest() {
        String postUrl = "http://localhost:8080/api/person";

        Person person = new Person("Ivan");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Отправляем запрос POST /person с параметрами id = null, name = 'Ivan'", () -> {
            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);

            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class
            );

            String putUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());

            HttpEntity<Person> putRequestEntity = new HttpEntity<>(new Person("ALO"), headers);
            restTemplate.put(putUrl, putRequestEntity);

            String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
            ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

            System.out.println(getResponseEntity);
        });


    }
}
