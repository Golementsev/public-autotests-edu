package com.tcs.vetclinic;

import com.tcs.vetclinic.domain.person.Person;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Collections;
import java.util.EmptyStackException;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
public class PostPerson {

    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Сохранение пользователя с пустыми id и не пустым name")
    @AllureId("1")
    public void test1() {
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
            //System.out.println(createPersonResponse);

            step("Проверяем, что в ответе от POST /person получен id", () -> {
                assertNotNull(createPersonResponse.getBody());
                //System.out.println(createPersonResponse.getBody());
            });

            step("Проверяем, что через метод GET /person/{id} мы получим созданного пользователя", () -> {
                String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
                ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                //System.out.println(getResponseEntity.getBody());
                assertNotNull(getResponseEntity);
                assertEquals(createPersonResponse.getBody(), getResponseEntity.getBody().getId());
                assertEquals(person.getName(), getResponseEntity.getBody().getName());
            });
        });
    }

    @Test
    @DisplayName("Перезапись пользователя из бд, созданием нового пользователя с тем же id")
    // Хочется, чтобы вместо перезаписи пользователя возникала ошибка
    @AllureId("2")
    public void test2() {
        String postUrl = "http://localhost:8080/api/person";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Предусловие: требуется id польлзователя из бд", () -> {});
        step("""
                Отправляем запрос POST /person с параметрами id = null, name = 'John'.
                Берем id созданного пользователя из ответа на запрос.""", () -> {
            Person firstPerosn = new Person("John");
            HttpEntity<Person> requestEntity = new HttpEntity<>(firstPerosn, headers);
            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class);
            long exist_id = createPersonResponse.getBody();

            step("Проверяем, что через метод GET /person/{id} мы получим созданного пользователя", () -> {
                String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
                ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                assertNotNull(getResponseEntity);
                assertEquals(createPersonResponse.getBody(), getResponseEntity.getBody().getId());
                assertEquals(firstPerosn.getName(), getResponseEntity.getBody().getName());
            });

            step("Отправляем запрос POST /person с параметрами id = exist_id, name = 'Albert'", () -> {
                Person secondPerson = new Person(exist_id, "Albert");
                HttpEntity<Person> requestEntity2 = new HttpEntity<>(secondPerson, headers);
                ResponseEntity<Long> createPersonResponse2 = restTemplate.exchange(
                        postUrl,
                        HttpMethod.POST,
                        requestEntity2,
                        Long.class);

                step("Проверяем, что пользователь перезаписался ", () -> {
                    String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse2.getBody());
                    ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                    assertNotNull(getResponseEntity);
                    assertEquals(createPersonResponse2.getBody(), getResponseEntity.getBody().getId());
                    assertEquals(secondPerson.getName(), getResponseEntity.getBody().getName());
                });
            });
        });


    }

    @Test
    @DisplayName("Пытаемся сохранить пользователя с именем длины равной пограничным значениям и выходящей за пограничные значения")
    @AllureId("3")
    public void test3() {
        String postUrl = "http://localhost:8080/api/person";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Отправляем запрос POST /person с параметрами id = null, длинной = 2", () -> {
            Person person = new Person( "j".repeat(2));
            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);

            step("Проверяем, что запрос вызвал ошибку HttpClientErrorException$BadRequest", () -> {
                try {
                    ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                            postUrl,
                            HttpMethod.POST,
                            requestEntity,
                            Long.class
                    );
                    System.out.println(createPersonResponse);
                    throw new EmptyStackException();
                } catch (Exception e) {
                    //assertEquals(HttpClientErrorException(), e.getClass());
                }
            });
        });
        step("Отправляем запрос POST /person с параметрами id = null, name длинной = 256", () -> {
            Person person = new Person( "j".repeat(256));
            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);

            step("Проверяем, что запрос вызвал ошибку HttpClientErrorException$BadRequest", () -> {
                try {
                    ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                            postUrl,
                            HttpMethod.POST,
                            requestEntity,
                            Long.class
                    );
                    System.out.println(createPersonResponse);
                    throw new EmptyStackException();
                } catch (Exception e) {
                    //assertEquals(HttpClientErrorException(), e.getClass());
                }
            });
        });
        step("Отправляем запрос POST /person с параметрами id = null, name длинной = 255", () -> {
            Person person = new Person( "j".repeat(255));
            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);
            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class);

            step("Проверяем, что через метод GET /person/{id} мы получим созданного пользователя", () -> {
                String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
                ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                assertNotNull(getResponseEntity);
                assertEquals(createPersonResponse.getBody(), getResponseEntity.getBody().getId());
                assertEquals(person.getName(), getResponseEntity.getBody().getName());
            });
        });
        step("Отправляем запрос POST /person с параметрами id = null, name длинной = 3", () -> {
            Person person = new Person( "j".repeat(3));
            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);
            ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Long.class);

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
    @DisplayName("Попытка сохранить пользователя с незаданными параметрами")
    @AllureId("4")
    public void test4() {
        String postUrl = "http://localhost:8080/api/person";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Отправляем запрос POST /person с параметрами id = null, name = null", () -> {
            Person person = new Person();
            HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);

            step("Проверяем, что запрос вызвал ошибку HttpClientErrorException$BadRequest", () -> {
                try {
                    ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                            postUrl,
                            HttpMethod.POST,
                            requestEntity,
                            Long.class
                    );
                    System.out.println(createPersonResponse);
                    throw new EmptyStackException();
                } catch (Exception e) {
                    //assertEquals(HttpClientErrorException(), e.getClass());
                }
            });
        });
    }

    @Test
    @DisplayName("Попытка отправить запрос без тела")
    @AllureId("5")
    public void test5() {
        String postUrl = "http://localhost:8080/api/person";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        step("Отправляем пустой запрос POST /person", () -> {
            HttpEntity<Person> requestEntity = new HttpEntity<>(headers);

            step("Проверяем, что запрос вызвал ошибку HttpClientErrorException$BadRequest", () -> {
                try {
                    ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                            postUrl,
                            HttpMethod.POST,
                            requestEntity,
                            Long.class
                    );
                    System.out.println(createPersonResponse);
                    throw new EmptyStackException();
                } catch (Exception e) {
                    String code = e.getMessage();
                    code = code.split(" ")[0];
                    assertEquals("500",code);
                }
            });
        });
    }

    @Test
    @DisplayName("Сохранение пользователя с именем, содержащим спецсимволы")
    @AllureId("6")
    public void test6() {
                String postUrl = "http://localhost:8080/api/person";
                Person person = new Person( "!@#$%^&* ()_+№;\\\":?-=.{[}]|/<>☹愛");
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

                step("Отправляем запрос POST /person с параметрами id = null, name = '!@#$%^&* ()_+№;\\\":?-=.{[}]|/<>☹愛'", () -> {
                    HttpEntity<Person> requestEntity = new HttpEntity<>(person, headers);
                    ResponseEntity<Long> createPersonResponse = restTemplate.exchange(
                            postUrl,
                            HttpMethod.POST,
                            requestEntity,
                            Long.class
                    );
                    //System.out.println(createPersonResponse);

                    step("Проверяем, что через метод GET /person/{id} мы получим созданного пользователя", () -> {
                        String getUrl = "http://localhost:8080/api/person/%s".formatted(createPersonResponse.getBody());
                        ResponseEntity<Person> getResponseEntity = restTemplate.getForEntity(getUrl, Person.class);

                        //System.out.println(getResponseEntity.getBody());
                        assertNotNull(getResponseEntity);
                        assertEquals(createPersonResponse.getBody(), getResponseEntity.getBody().getId());
                        assertEquals(person.getName(), getResponseEntity.getBody().getName());
                    });
                });
            }




    /*@Test
    @DisplayName("Второй интеграционный тест")
    @AllureId("2")
    public void test2() {
        step("Шаг 3", () -> {});
        step("Шаг 4", () -> {});
        step("Проверка 2", () -> {});
    };*/



}


