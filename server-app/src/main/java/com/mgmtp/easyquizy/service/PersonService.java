package com.mgmtp.easyquizy.service;

import com.mgmtp.easyquizy.model.PersonEntity;
import com.mgmtp.easyquizy.exception.RecordNotFoundException;
import com.mgmtp.easyquizy.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;

    /**
     * Retrive all person data
     *
     * @return List all of person
     */
    public List<PersonEntity> getAllPersons() {
        List<PersonEntity> personList = repository.findAll();

        if(!personList.isEmpty()) {
            return personList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Retrive single person information by id
     *
     * @param id Person id
     * @return person's information by id
     */
    public PersonEntity getPersonById(Long id) throws RecordNotFoundException {
        Optional<PersonEntity> person = repository.findById(id);

        if(person.isPresent()) {
            return person.get();
        } else {
            log.debug("No Person record exist for given id: {}", id);
            throw new RecordNotFoundException("No Person record exist for given id");
        }
    }

    /**
     * Create/Update person information
     *
     * @param entity information for insert or update
     * @return update result
     */
    public PersonEntity createOrUpdatePerson(PersonEntity entity) throws RecordNotFoundException {
        Optional<PersonEntity> person;
        if (entity.getId() != null) {
            person = repository.findById(entity.getId());
        } else {
            person = Optional.empty();
        }

        if (person.isPresent()) {
            PersonEntity newEntity = person.get();
            newEntity.setEmail(entity.getEmail());
            newEntity.setFirstName(entity.getFirstName());
            newEntity.setLastName(entity.getLastName());

            newEntity = repository.save(newEntity);

            return newEntity;
        } else {
            entity = repository.save(entity);

            return entity;
        }
    }

    /**
     * Delete Person by id
     *
     * @param id Person id
     */
    public void deletePersonById(Long id) throws RecordNotFoundException {
        Optional<PersonEntity> person = repository.findById(id);

        if(person.isPresent()) {
            repository.deleteById(id);
        } else {
            log.debug("No Person record exist for given id: {}", id);
            throw new RecordNotFoundException("No Person record exist for given id");
        }
    }
}
