package com.tcs.vetclinic;

import com.tcs.vetclinic.domain.person.Person;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "TestPersonClient", url = "http://localhost:8080/api")
public interface TestingPersonClient {

    @GetMapping("/person/{id}")
    Person findById(@PathVariable("id") Long id);

    @GetMapping("/person")
    List<Person> findAll(@RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort);

    @PostMapping("/person")
    Long create(@RequestBody Person person);

    @PutMapping("/person/{id}")
    void updateById(@PathVariable("id") Long id, @RequestBody Person person);

    @DeleteMapping("/person/{id}")
    void delete(@PathVariable("id") Long id);
}
