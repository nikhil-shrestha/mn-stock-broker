package illusionist.broker.persistence.jpa;

import java.util.List;

import illusionist.broker.persistence.model.SymbolEntity;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface SymbolsRepository extends CrudRepository<SymbolEntity, Integer> {

  @Override
  List<SymbolEntity> findAll();

}