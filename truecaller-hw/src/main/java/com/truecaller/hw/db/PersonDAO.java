package com.truecaller.hw.db;

import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import com.google.common.base.Optional;
import com.truecaller.hw.core.Person;

public class PersonDAO extends AbstractDAO<Person> 
{
    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Person> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public Person create(Person person) {
        return persist(person);
    }
    
    public Person update(Person person) {
        return persist(person);
    }
}