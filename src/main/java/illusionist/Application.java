package illusionist;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;

@OpenAPIDefinition(
  info = @Info(
    title = "mn-stock-broker",
    version = "0.1",
    description = "Udemy Micronaut Course"
  )
)
public class Application {

  public static void main(String[] args) {
    Micronaut.run(Application.class, args);
  }
}
