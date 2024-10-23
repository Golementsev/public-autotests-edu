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
@Story("PUT /person/{id}")
public class PutChangePersonByIDTests extends BaseIntegrationTest {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Изменение имени пользователя по id")
    @AllureId("1")
    public void ChangeNameByID(){

        step("Создаем пользователя при помощи POST name = 'Ivan' и сохраняем его id", () -> {

        });
        step("Отправляем запрос PUT /person/{id}, name = 'Sanek'", () -> {

            step("Проверяем, что GET /person/{id} возвращает name = 'Sanek'", () -> {

            });
        });
    };

    @Test
    @DisplayName("Изменение имени пользователя по несуществующему id")
    @AllureId("2")
    public void ChangeNameByNotExistedID(){

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

                step("Проверяем, что PUT /person/{id} возращает ошибку 404", () -> {
                    assertThrows(FeignException.NotFound.class, () -> testPersonClient.updateById(id, new Person(id, "Igorek")));
                });
            });
        });
    };
}
