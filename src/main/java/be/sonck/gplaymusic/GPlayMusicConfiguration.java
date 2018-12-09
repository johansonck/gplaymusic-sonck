package be.sonck.gplaymusic;

import com.github.felixgail.gplaymusic.api.GPlayMusic;
import com.github.felixgail.gplaymusic.exceptions.InitializationException;
import com.github.felixgail.gplaymusic.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import svarzee.gps.gpsoauth.AuthToken;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class GPlayMusicConfiguration {

    @Value("${auth.username}")
    String userName;

    @Value("${auth.password}")
    String password;

    @Value("${auth.android_id}")
    String androidId;

    @Value("${auth.token}")
    String token;

    @Bean
    public GPlayMusic gPlayMusic() {
        Optional<GPlayMusic> gPlayMusic = loginWithExistingToken();
        if (gPlayMusic.isPresent()) return gPlayMusic.get();

        gPlayMusic = loginWithNewToken();
        if (gPlayMusic.isPresent()) return gPlayMusic.get();

        throw new IllegalStateException("Could not login");
    }

    private Optional<GPlayMusic> loginWithExistingToken() {
        if (StringUtils.isEmpty(token)) return Optional.empty();

        try {
            return Optional.of(login(TokenProvider.provideToken(token)));

        } catch (InitializationException e) {
            log.warn("Initialization with existing token failed", e);
            return Optional.empty();
        }
    }

    private Optional<GPlayMusic> loginWithNewToken() {
        try {
            return Optional.of(login(getNewAuthToken()));

        } catch (InitializationException e) {
            log.error("Initialization with new token failed", e);
            return Optional.empty();
        }
    }

    private AuthToken getNewAuthToken() {
        try {
            AuthToken authToken = TokenProvider.provideToken(userName, password, androidId);
            log.info("Created new token {}", authToken.getToken());
            return authToken;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private GPlayMusic login(AuthToken authToken) {
        //Investigate: While cleanly running on pc. CircleCI will throw TimeOutExceptions. Increase readTimeout for now.
        OkHttpClient.Builder builder = GPlayMusic.Builder.getDefaultHttpBuilder().readTimeout(30, TimeUnit.SECONDS);

        return new GPlayMusic.Builder()
                .setAuthToken(authToken)
                .setDebug(false)
                .setHttpClientBuilder(builder)
                .build();
    }
}
