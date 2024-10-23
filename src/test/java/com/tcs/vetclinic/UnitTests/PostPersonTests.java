package com.tcs.vetclinic.UnitTests;

import com.tcs.vetclinic.BaseIntegrationTest;
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
public class PostPersonTests extends BaseIntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Сохранение пользователя с пустыми id и name длинной от 3 до 255")
    @AllureId("1")
    public void CreatePersonWithoutID() {
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

            step("Проверяем, что в ответе от POST /person получен id", () -> {
                assertNotNull(createPersonResponse.getBody());
            });

            step("Проверяем, что через метод GET /person/{id} мы получим созданного пользователя", () -> {
                String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
                ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                assertNotNull(getResponseEntity);
                assertEquals(createPersonResponse.getBody(), getResponseEntity.getBody().getId());
                assertEquals(person.getName(), getResponseEntity.getBody().getName());
            });
        });
    }

    @Test
    @DisplayName("сохранение пользователя с именем длинной от 3 до 255 и неуникальным id")
    @AllureId("2")
    public void CreatePersonWithNameAndNonUniqueID() {
        String postUrl = "http://localhost:8080/api/person";

        Person person = new Person("Sanek");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Создаем человека POST /person с параметрами name = 'Sanek'", () -> {

            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);

            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class
            );

            step("Создаем человека с таким же id", () -> {
                Person person1 = new Person(createPersonResponse.getBody(), "Igorek");
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

                step("Проверяем что POST person создал человека и присвоил другой id", () -> {
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
    @DisplayName("сохранение пользователя с именем длинной < 3")
    @AllureId("3")
    public void CreatePersonWithIncorrectSmallName() {
        step("Вызываю POST /person с name 'a'",
                () -> step("Проверяю, что в ответ пришла 400 Bad Request",
                        () -> assertThrows(FeignException.BadRequest.class, () -> testPersonClient.create(new Person("a"))))
        );
    };

    @Test
    @DisplayName("сохранение пользователя с именем длинной > 255")
    @AllureId("4")
    public void CreatePersonWithIncorrectBigName() {
        step("Вызываю POST /person с name 'a'*300",
                () -> step("Проверяю, что в ответ пришла 400 Bad Request",
                        () -> assertThrows(FeignException.BadRequest.class, () -> testPersonClient.create(new Person("a".repeat(300)))))
        );
    };

    @Test
    @DisplayName("сохранение пользователя с пустым name")
    @AllureId("5")
    public void createPersonWithoutNameTest() {
        step("Вызываю POST /person с пустым name",
                () -> step("Проверяю, что в ответ пришла 400 Bad Request",
                        () -> assertThrows(FeignException.BadRequest.class, () -> testPersonClient.create(new Person())))
        );
    };
}