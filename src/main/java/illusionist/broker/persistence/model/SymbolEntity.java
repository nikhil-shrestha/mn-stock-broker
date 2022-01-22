package illusionist.broker.persistence.model;


import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "symbol")
@Table(name = "symbols")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymbolEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String value;

  public SymbolEntity(String value) {
    this.value = value;
  }
}