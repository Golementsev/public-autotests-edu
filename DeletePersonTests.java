package com.tcs.vetclinic;

import com.tcs.vetclinic.IntegrationTest;
import com.tcs.vetclinic.domain.person.Person;
import feign.FeignException;
import io.qameta.allure.AllureId;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Epic("publicApi")
@Feature("person controller")
@Story("DELETE /person/{id}")
public class DeletePersonTests extends IntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Удаление пользователя по его id")
    @AllureId("1")
    public void DeletePersonById() {

        step("Создаем пользователя с помощью POST /person с параметрами: id = 100, name = 'Paul'", () -> {

        });

        step("Отправляем запрос DELETE /person{id}, id = 150", () -> {

            step("Проверяем, что GET /person/{id} с id = 150 возвращает ошибку", () -> {

            });

        });
    }

    ;

    @Test
    @DisplayName("Получение ошибки 409 при удалении пользователя c несуществующим id")
    @AllureId("2")
    public void DeletePersonByNotId() {

        String getUrl = "http://localhost:8080/api/person/";

        step("Создаем пользователя с помощью метода POST /person и сохраняем его id", () -> {
            String postUrl = "http://localhost:8080/api/person";

            Person person = new Person("Paul");
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

                step("Проверяем, что при повторном удалении с помощью DELETE /person/{id} возращается ошибка 409", () -> {
                    assertThrows(FeignException.Conflict.class, () -> TestPersonClient.delete(id));
                });
            });
        });
    }
;
}
