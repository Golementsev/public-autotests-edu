package com.tcs.vetclinic.api;

import com.tcs.vetclinic.domain.person.Person;
import com.tcs.vetclinic.domain.sort.SortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/person")
@Tag(name = "person", description = "Api для работы с перс. данными клиентов")
public interface PersonClient {

    @GetMapping
    @Operation(summary = "Информация о всех клиентах клиники")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о клиентах клиники",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Person.class)))}),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    Iterable<Person> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ASC") SortType sort);

    @GetMapping("/{id}")
    @Operation(summary = "Информация о конкретном клиенте клиники")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о клиенте",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content)
    })
    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(type = "integer"),
            description = "Идентификатор клиента в базе")
    Person findById(@PositiveOrZero @NotNull @PathVariable long id);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Метод добавления нового клиента клиники")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиент добавлен в базу",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "integer"))}),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    Long create(@Valid @RequestBody Person person);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Измените статус на NO_CONTENT
    @Operation(summary = "Удаление данных клиента из базы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Клиент удален из базы", content = @Content),
            @ApiResponse(responseCode = "409", description = "Такого клиента нет в базе", content = @Content)
    })
    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(type = "integer"),
            description = "Идентификатор клиента в базе")
    void delete(@PositiveOrZero @NotNull @PathVariable long id);

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Обновление данных клиента в базе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Клиент обновлен в базе", content = @Content),
            @ApiResponse(responseCode = "404", description = "Такого клиента нет в базе", content = @Content)
    })
    @Parameter(name = "id", in = ParameterIn.PATH, schema = @Schema(type = "integer"),
            description = "Идентификатор клиента в базе")
    void updateById(@PositiveOrZero @NotNull @PathVariable long id, @Valid @RequestBody Person personDto);
}
