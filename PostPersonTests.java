package com.tcs.vetclinic;

import com.tcs.vetclinic.IntegrationTest;
import com.tcs.vetclinic.domain.person.Person;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static io.qameta.allure.Allure.step;
import feign.FeignException;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@Epic("publicApi")
@Feature("person controller")
@Story("POST /person")
public class PostPersonTests extends IntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Сохраняем пользователя с параметрами: id = null, name: длина - от 2 до 255")
    @AllureId("1")

    public void CreatePersonEmptyIdAndLongName() {
        String postUrl = "http://localhost:8080/api/person";

        Person person = new Person("Bob");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Отправляем запрос POST /person с параметрами: id = null, name = 'Bob'", () -> {
            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);

            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class
            );

            step("Проверяем, что в ответ от запроса POST /person получаем id пользователя", () -> {
                assertNotNull(createPersonResponse.getBody());
            });

            step("Проверяем, что с помощью GET /person/{id} мы получаем созданного пользователя", () -> {
                String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
                ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                assertNotNull(getResponseEntity);
                assertEquals(createPersonResponse.getBody(), getResponseEntity.getBody().getId());
                assertEquals(person.getName(), getResponseEntity.getBody().getName());
            });
        });
    }

    @Test
    @DisplayName("Сохранение пользователя с именем - длиной от 2 до 255 и неуникальным id")
    @AllureId("2")
    public void CreatePersonWithLongNameAndNonUniqueId() {
        String postUrl = "http://localhost:8080/api/person";

        Person person = new Person("Sam");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Создаем пользователя с помощью POST /person с параметром: name = 'Sam'", () -> {

            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);

            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class
            );

            step("Создаем пользователя с помощью POST /person c аналогичным id", () -> {
                Person person1 = new Person(createPersonResponse.getBody(), "Dan");
                HttpHeaders headers1 = new HttpHeaders();
                headers1.setContentType(MediaType.APPLICATION_JSON);
                headers1.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

                HttpEntity<Person> requestEntity1 = new HttpEntity<>(person1, headers1);

                ResponseEntity<Long> createPersonResponse1 = restTemplate.exchange(
                        postUrl,
                        HttpMethod.POST,
                        requestEntity1,
                        Long.class
                );

                step("Проверяем, что с помощью POST /person создался пользователь и ему присвоился другой id", () -> {
                    String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse1.getBody());
                    ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                    assertNotNull(getResponseEntity);
                    assertEquals(createPersonResponse1.getBody(), getResponseEntity.getBody().getId());
                    assertEquals(person1.getName(), getResponseEntity.getBody().getName());

                    assertNotEquals(createPersonResponse1.getBody(), createPersonResponse.getBody());
                });
            });
        });
    }

    @Test
    @DisplayName("Сохранение пользователя с именем - длиной < 2")
    @AllureId("3")
    public void CreatePersonWithShortName() {
        step("Вызываем метод POST /person с параметром: name = 'abc'",
                () -> step("Проверяем, что в ответ на запрос пришла ошибка - 400 Bad Request",
                        () -> assertThrows(FeignException.BadRequest.class, () -> TestPersonClient.create(new Person("abc"))))
        );
    }

    ;

    @Test
    @DisplayName("Сохранение пользователя с именем длиной > 255")
    @AllureId("4")
    public void CreatePersonWithVeryLongName() {
        step("Вызываем метод POST /person с параметром: name = 'abc'*200",
                () -> step("Проверяем, что в ответ на запрос пришла ошибка - 400 Bad Request",
                        () -> assertThrows(FeignException.BadRequest.class, () -> TestPersonClient.create(new Person("abc".repeat(200)))))
        );
    }

    ;

    @Test
    @DisplayName("Сохранение пользователя с пустым name")
    @AllureId("5")
    public void CreatePersonWithEmptyName() {
        step("Вызываем запрос POST /person с параметром: name = null",
                () -> step("Проверяем, что в ответ на запрос пришла ошибка - 400 Bad Request",
                        () -> assertThrows(FeignException.BadRequest.class, () -> TestPersonClient.create(new Person())))
        );
    }
;
}
