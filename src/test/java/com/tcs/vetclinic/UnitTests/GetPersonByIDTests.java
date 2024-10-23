package com.tcs.vetclinic.UnitTests;

import com.tcs.vetclinic.BaseIntegrationTest;
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
public class GetPersonByIDTests extends BaseIntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Получение пользователя по существующему id")
    @AllureId("1")
    public void GetPersonWithID() {

        String postUrl = "http://localhost:8080/api/person";

        String userName = "Igorek";

        step("Создаем человека при помощи POST /person с параметрами name = '" + userName + "'", () -> {
            // Создаем объект для отправки
            Person personToCreate = new Person(userName); // Предполагаем, что ваш класс Person имеет такой конструктор

            // Настраиваем заголовки
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Person> requestEntity = new HttpEntity<>(personToCreate, headers);

            // Выполняем POST-запрос
            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class
            );

            step("Проверяем, что в ответе от POST /person получен id", () -> {
                assertNotNull(createPersonResponse.getBody());
            });

            step("Отправляем GET person/{id} по только что созданному айдишнику", () -> {
                String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
                ResponseEntity<Person> getResponse = restTemplate.getForEntity(getUrl, Person.class);

                step("Проверяем, что возвращается id и name", () -> {
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
    public void GetPersonWithNotExistedID() {

        String getUrl = "http://localhost:8080/api/person/";

        step("Создаем пользователя при помощи POST /person и сохраняем его id", () -> {
            String postUrl = "http://localhost:8080/api/person";

            Person person = new Person("Ivan");
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

            step("Удалаяем этого пользователя при помощи DELETE /person/{id}", () -> {
                String deleteUrl = getUrl + id;
                restTemplate.delete(deleteUrl);

                step("Проверяем, что GET /person/{id} возращает ошибку 404", () -> {
                    assertThrows(FeignException.NotFound.class, () -> testPersonClient.findById(id));
                });
            });
        });
    };
};