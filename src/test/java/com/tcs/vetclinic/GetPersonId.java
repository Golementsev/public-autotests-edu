package com.tcs.vetclinic;

import com.tcs.vetclinic.domain.person.Person;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.lang.Math;
import java.util.Collections;
import java.util.EmptyStackException;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetPersonId {
    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("получение существующего пользователя")
    @AllureId("10")
    public void test1() {
        String postUrl = "http://localhost:8080/api/person";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Предусловие: требуется id существующего пользователя ", () -> {});
        step("""
                Отправляем запрос POST /person с параметрами id = null, name = 'John'.
                Берем id созданного пользователя из ответа на запрос.""", () -> {

            Person person = new Person("John");
            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);
            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class);
            long exist_id = createPersonResponse.getBody();

            step("Проверяем, что через метод GET /person/{id} мы получим созданного пользователя", () -> {
                String getUrl = "http://localhost:8080/api/person/%s".formatted(exist_id);
                ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                //System.out.println(getResponseEntity.getBody());
                assertNotNull(getResponseEntity);
                assertEquals(createPersonResponse.getBody(), getResponseEntity.getBody().getId());
                assertEquals(person.getName(), getResponseEntity.getBody().getName());
            });

        });
    }

    @Test
    @DisplayName("Попытка получить несуществующего пользователя")
    @AllureId("11")
    public void test2() {
            step("Вы", () -> {
                Long i = 0L;
                Exception exp = new Exception();
                do {
                    String getUrl = "http://localhost:8080/api/person/%s".formatted(i);
                    try {
                        ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);
                    } catch (Exception e) {
                        exp = e;
                    }
                } while (exp.equals(new Exception()) && i < Math.pow(2, 63));
                String code = exp.getMessage();
                code = code.split(" ")[0];
                assertEquals("404",code);
            });
    }

    @Test
    @DisplayName("Попытка указать нечисловое значение id")
    @AllureId("12")
    public void test3() {
        step("""
                Подставляем вместо id строку 'exist_id'. Отправляем запрос. Вызываем ошибку,
                в случае успешного выполнения и проверяем на правильность код ошибки в случае её возникновения.""", () -> {
            String getUrl = "http://localhost:8080/api/person/%s".formatted("exist_id");
            try {
                ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);
                throw new EmptyStackException();
            } catch (Exception e) {
                String code = e.getMessage();
                code = code.split(" ")[0];
                assertEquals("500",code);
            }

        });

    }
}
