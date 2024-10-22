package com.tcs.vetclinic.integrationTests;

import com.tcs.vetclinic.BaseIntegrationTest;
import com.tcs.vetclinic.domain.person.Person;
import io.qameta.allure.AllureId;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("publicApi")
@Feature("person controller")
@Story("GET /person/{id}")
public class GetPersonTest extends BaseIntegrationTest {
    @Test
    @DisplayName("Получение существующего пользователя по id")
    @AllureId("3")
    public void createPersonWithoutNameTest() {
        var personDto = new Person(RandomString.make());

        var personId = step("Precondition: Вызываю POST /person с пустым name",
                () -> testPersonClient.create(personDto));

        step("Вызываем GET /person/{id}", () -> {
            var personClientById = testPersonClient.findById(personId);

            step("Проверяем, что в ответе person.name = %s".formatted(personDto.getName()), () -> {
                assertEquals(personDto.getName(), personClientById.getName());
            });
        });
    }
}
