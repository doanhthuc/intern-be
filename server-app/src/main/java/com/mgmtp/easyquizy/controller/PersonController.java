package com.mgmtp.easyquizy.controller;

import com.mgmtp.easyquizy.dto.PersonDTO;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.model.PersonEntity;
import com.mgmtp.easyquizy.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService service;

    /**
     * Retrive all person data
     *
     * @return List all of person
     */
    @GetMapping
    public ResponseEntity<List<PersonEntity>> getAllPersons() {
        List<PersonEntity> list = service.getAllPersons();

        return ResponseEntity.ok(list);
    }

    /**
     * Retrive single person information by id
     *
     * @param id Person id
     * @return person's information by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonEntity> getPersonById(@PathVariable("id") Long id)
            throws RecordNotFoundException {
        PersonEntity entity = service.getPersonById(id);

        return ResponseEntity.ok(entity);
    }

    /**
     * Create/Update person information
     *
     * @param person information for insert or update
     * @return update result
     */
    @PostMapping(
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<PersonEntity> createOrUpdatePerson(@RequestBody PersonDTO person)
            throws RecordNotFoundException {
        PersonEntity entity = new PersonEntity();
        BeanUtils.copyProperties(person, entity);
        PersonEntity updated = service.createOrUpdatePerson(entity);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete Person by id
     *
     * @param id Person id
     * @return DELETED
     */
    @DeleteMapping("/{id}")
    public String deletePersonById(@PathVariable("id") Long id)
            throws RecordNotFoundException {
        service.deletePersonById(id);
        return "DELETED";
    }
}
