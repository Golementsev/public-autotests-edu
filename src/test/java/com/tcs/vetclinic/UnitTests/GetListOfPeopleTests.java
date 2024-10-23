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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Epic("publicApi")
@Feature("person controller")
@Story("GET /person")
public class GetListOfPeopleTests extends BaseIntegrationTest {
    String baseUrl = "http://localhost:8080/api/person?page=%d&size=%d&sort=%s";
    // ASC DESC
    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Получение списка пользователей и проверка последнего созданного пользователя")
    @AllureId("1")
    public void GetPeopleList() {
        step("Создаем пользователя", () -> {
            String postUrl = "http://localhost:8080/api/person";
            String name = "Magomed";

            Person person = new Person(name);
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

            step("Отправляем запрос GET /person и получям список из последнего созданного юзера", () -> {
                String getUrl = baseUrl.formatted(0, 1, "DESC");
                ResponseEntity<List<Person>> getResponseEntity = restTemplate.exchange(getUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Person>>() {});

                assertNotNull(getResponseEntity);

                step("Проверяем, что пользователи совпадают", () -> {
                    assertEquals(createPersonResponse.getBody(), getResponseEntity.getBody().get(0).getId());
                    assertEquals(name, getResponseEntity.getBody().get(0).getName());
                });
            });
        });
    };

    @Test
    @DisplayName("Получение списка пользователей и проверка последнего созданного пользователя")
    @AllureId("2")
    public void GetPeopleWithWrongData() {
        step("Передаем page и size < 0 и строки несоответствующие сортировкам", () -> {
            assertThrows(FeignException.InternalServerError.class, () -> testPersonClient.findAll(-1, 10, "ABS"));
            assertThrows(FeignException.InternalServerError.class, () -> testPersonClient.findAll(1, -10, "ABS"));
            assertThrows(FeignException.InternalServerError.class, () -> testPersonClient.findAll(1, 10, "A"));
        });
    };
}
