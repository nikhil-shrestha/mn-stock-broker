package illusionist.broker.error;

import illusionist.broker.api.RestApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomError implements RestApiResponse {
  int status;
  String error;
  String message;
}
