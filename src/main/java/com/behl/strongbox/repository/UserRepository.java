package com.behl.strongbox.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.behl.strongbox.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {

	boolean existsByUserName(final String userName);

	Optional<User> findByUserName(final String userName);

}