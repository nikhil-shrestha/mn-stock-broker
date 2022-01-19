package illusionist.auth.jwt;

import illusionist.broker.store.InMemoryStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProviderUserPassword.class);

  @Override
  public Publisher<AuthenticationResponse> authenticate(
    HttpRequest<?> httpRequest,
    AuthenticationRequest<?, ?> authenticationRequest) {

    return Flowable.create(emitter -> {
      final Object identity = authenticationRequest.getIdentity();
      final Object secret = authenticationRequest.getSecret();
      LOG.debug("User {} tries to login...", identity);

      if (identity.equals("my-user") && secret.equals("secret")){
        // pass
        LOG.debug("User logged in.");
        emitter.onNext(AuthenticationResponse.success((String) identity, new ArrayList<>()));
        emitter.onComplete();
        return;
      }

      emitter.onError(new AuthenticationException(new AuthenticationFailed("Wrong username or password!")));
    }, BackpressureStrategy.ERROR);
  }
}
