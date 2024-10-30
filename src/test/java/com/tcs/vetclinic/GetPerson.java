package com.tcs.vetclinic;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GetPerson {

    @Test
    @DisplayName("Проверка работы параметров page и size")
    @AllureId("1")
    public void test1() {
        Allure.step("Проверить, что в базе есть 55 и больше пользователей. Создать недостающих, если их меньше 55");
        Allure.step("Отправить запрос GET /person&page=3&size=11", () -> {});
        Allure.step("Проверка ответа: код=200; ответ должен содержать 11 значений.", () -> {});
        Allure.step("Отправить запрос Get /person&page=4&size=13", () -> {});
        Allure.step("Проверка ответа: ", () -> {});
    }

    @Test
    @DisplayName("Второй интеграционный тест")
    @AllureId("2")
    public void test2() {
        Allure.step("Шаг 3", () -> {});
        Allure.step("Шаг 4", () -> {});
        Allure.step("Проверка 2", () -> {});
    }
}
