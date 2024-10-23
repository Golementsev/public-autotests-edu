package com.tcs.vetclinic.UnitTests;

import com.tcs.vetclinic.BaseIntegrationTest;
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
public class DeletePersonByIDTests extends BaseIntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Удаление пользователя по id")
    @AllureId("1")
    public void DeletePersonByID() {

        step("Создаем человека POST /person id = 333, name = 'Ivan'", () -> {

        });

        step("Отправляем запрос DELETE /person{id}, id = 333", () -> {

            step("Проверяем, что GET /person/{id} id = 333 возвращает ошибку", () -> {

            });

        });
    };

    @Test
    @DisplayName("Удаление пользователя по несуществующему id")
    @AllureId("2")
    public void DeletePersonByNotExistedID() {

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

            step("Удалаяем этого пользователя при помощи запроса DELETE /person/{id}", () -> {
                String deleteUrl = getUrl + id;
                restTemplate.delete(deleteUrl);

                step("Проверяем, что провторное удаление DELETE /person/{id} возращает ошибку 409", () -> {
                    assertThrows(FeignException.Conflict.class, () -> testPersonClient.delete(id));
                });
            });
        });
    };
}
