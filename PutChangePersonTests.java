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
@Story("PUT /person/{id}")
public class PutChangePersonTests extends IntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Изменение имени пользователя по его id")
    @AllureId("1")
    public void ChangeNameById() {

        step("Создаем пользователя с помощью запроса POST: name = 'Bim' и сохраняем его id", () -> {
            String postUrl = "http://localhost:8080/api/person";

            Person person = new Person("Bim");
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
        });
        step("Отправляем запрос PUT /person/{id}, name = 'Mitt'", () -> {

            step("Проверяем, что метод GET /person/{id} возвращает name = 'Mitt'", () -> {

            });
        });
    }

    ;

    @Test
    @DisplayName("Изменение имени пользователя по отсутствующему id")
    @AllureId("2")
    public void ChangeNameByMissingId() {

        String getUrl = "http://localhost:8080/api/person/";

        step("Создаем пользователя с помощью метода POST /person и сохраняем его id", () -> {
            String postUrl = "http://localhost:8080/api/person";

            Person person = new Person("Kim");
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

            step("Удаляем пользователя с помощью DELETE /person/{id}", () -> {
                String deleteUrl = getUrl + id;
                restTemplate.delete(deleteUrl);

                step("Проверяем, что PUT /person/{id} возращает ошибку 404", () -> {
                    assertThrows(FeignException.NotFound.class, () -> TestPersonClient.updateById(id, new Person(id, "Dan")));
                });
            });
        });
    }
;
}
