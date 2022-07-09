package com.behl.strongbox.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.behl.strongbox.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	boolean existsByUserName(final String userName);

	Optional<User> findByUserName(final String userName);

}