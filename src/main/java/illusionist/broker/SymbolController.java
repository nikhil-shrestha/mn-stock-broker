package illusionist.broker;

import illusionist.broker.model.Symbol;
import illusionist.broker.store.InMemoryStore;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller("/symbols")
public class SymbolController {

  private final InMemoryStore memoryStore;

  public SymbolController(InMemoryStore memoryStore) {
    this.memoryStore = memoryStore;
  }

  @Get
  public List<Symbol> getAll(){
    return new ArrayList<>(memoryStore.getSymbols().values());
  }

  @Get("{value}")
  public Symbol getSymbolByValue(@PathVariable String value){
    return memoryStore.getSymbols().get(value);
  }

  @Get("/filter{?max,offset}")
  public List<Symbol> getSymbols(@QueryValue Optional<Integer> max, @QueryValue Optional<Integer> offset){
    return memoryStore.getSymbols().values()
      .stream()
      .skip(offset.orElse(0))
      .limit(max.orElse(0))
      .collect(Collectors.toList());
  }
}
