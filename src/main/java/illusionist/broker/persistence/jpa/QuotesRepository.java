package illusionist.broker.persistence.jpa;

import java.util.List;
import java.util.Optional;

import illusionist.broker.persistence.model.QuoteEntity;
import illusionist.broker.persistence.model.SymbolEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface QuotesRepository extends CrudRepository<QuoteEntity, Integer> {

  @Override
  List<QuoteEntity> findAll();

  Optional<QuoteEntity> findBySymbolValue(String entity);

  // Ordering
  List<QuoteEntity> listOrderByVolumeDesc();

  List<QuoteEntity> listOrderByVolumeAsc();
}
