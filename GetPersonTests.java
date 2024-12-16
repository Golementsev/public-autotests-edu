package com.tcs.vetclinic;

import com.tcs.vetclinic.IntegrationTest;
import com.tcs.vetclinic.domain.person.Person;
import feign.FeignException;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static io.qameta.allure.Allure.step;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Epic("publicApi")
@Feature("person controller")
@Story("Get /person/{id}")
public class GetPersonTests extends IntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Получение пользователя по существующему id")
    @AllureId("1")
    public void GetPersonWithId() {

        String postUrl = "http://localhost:8080/api/person";

        String userName = "Alpha";

        step("Создаем пользователя с помощью метода POST /person с параметрами: name = '" + userName + "'", () -> {
            Person personToCreate = new Person(userName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Person> requestEntity = new HttpEntity<>(personToCreate, headers);

            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class
            );

            step("Проверяем, что в ответ от запроса с помощью POST /person получаем id пользователя", () -> {
                assertNotNull(createPersonResponse.getBody());
            });
            step("Отправляем GET person/{id} по созданному id пользователя", () -> {
                String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
                ResponseEntity<Person> getResponse = restTemplate.getForEntity(getUrl, Person.class);

                step("Проверяем, что запрос возвращает id и name пользователя", () -> {
                    assertEquals(HttpStatus.OK, getResponse.getStatusCode());

                    Person retrievedPerson = getResponse.getBody();
                    assertNotNull(retrievedPerson);
                    assertEquals(createPersonResponse.getBody(), retrievedPerson.getId());
                    assertEquals(userName, retrievedPerson.getName());
                });
            });
        });
    }

    @Test
    @DisplayName("Получение пользователя по существующему id")
    @AllureId("2")
    public void GetPersonWithAnotherId() {

        String getUrl = "http://localhost:8080/api/person/";

        step("Создаем пользователя с помощью метода POST /person и сохраняем его id", () -> {
            String postUrl = "http://localhost:8080/api/person";

            Person person = new Person("Ben");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);

            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class
            );

            long id = createPersonResponse.getBody();

            step("Удаляем пользователя с помощью метода DELETE /person/{id}", () -> {
                String deleteUrl = getUrl + id;
                restTemplate.delete(deleteUrl);

                step("Проверяем, что метод GET /person/{id} возращает ошибку 404", () -> {
                    assertThrows(FeignException.NotFound.class, () -> testPersonClient.findById(id));
                });
            });
        });
    }
;
};
