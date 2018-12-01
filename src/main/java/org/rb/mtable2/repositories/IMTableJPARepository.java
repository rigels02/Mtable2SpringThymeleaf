package org.rb.mtable2.repositories;

import org.rb.mtable2.model.MTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="/mytables",collectionResourceRel="mytables")
public interface IMTableJPARepository extends JpaRepository<MTable, Long> {

}
