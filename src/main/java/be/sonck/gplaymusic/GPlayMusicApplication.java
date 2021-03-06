package be.sonck.gplaymusic;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class GPlayMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(GPlayMusicApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            ZeppaEllaPlaylistCreator zeppaEllaPlaylistCreator = ctx.getBean(ZeppaEllaPlaylistCreator.class);
            zeppaEllaPlaylistCreator.refreshPlaylist();
        };
    }
}
