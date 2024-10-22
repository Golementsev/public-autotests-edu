package com.tcs.vetclinic.integrationTests;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

import com.tcs.vetclinic.BaseIntegrationTest;
import com.tcs.vetclinic.domain.person.Person;
import feign.FeignException;
import io.qameta.allure.AllureId;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Epic("publicApi")
@Feature("person controller")
@Story("POST /person")
public class CreatePersonTest extends BaseIntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Сохранение пользователя с пустыми id и не пустым name")
    @AllureId("1")
    public void createPersonWithNameTest() {
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
    @DisplayName("Ошибка при сохранении пользователя с пустым name")
    @AllureId("2")
    public void createPersonWithoutNameTest() {
        step("Вызываю POST /person с пустым name",
                () -> step("Проверяю, что в ответ пришла 400 Bad Request",
                        () -> assertThrows(FeignException.BadRequest.class, () -> testPersonClient.create(new Person())))
        );
    }


}
