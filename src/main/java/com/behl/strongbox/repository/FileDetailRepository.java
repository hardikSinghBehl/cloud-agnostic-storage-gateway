package com.behl.strongbox.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.behl.strongbox.entity.FileDetail;

@Repository
public interface FileDetailRepository extends MongoRepository<FileDetail, UUID> {

}
