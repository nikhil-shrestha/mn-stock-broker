package illusionist.broker.error;

import illusionist.broker.api.RestApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomError implements RestApiResponse {
  private int status;
  private String error;
  private String message;
}
