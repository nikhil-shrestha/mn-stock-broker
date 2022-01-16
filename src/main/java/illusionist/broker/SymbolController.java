package illusionist.broker;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.ArrayList;
import java.util.List;

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
}
