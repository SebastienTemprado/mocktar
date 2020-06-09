package fr.stemprado.apps.mocktar.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.stemprado.apps.mocktar.beans.Mock;

public interface MockRepository extends MongoRepository<Mock, Long>, MockRepositoryCustom {
    Mock findByName(String name);
}