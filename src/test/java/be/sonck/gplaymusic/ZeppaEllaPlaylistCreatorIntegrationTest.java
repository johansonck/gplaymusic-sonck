package be.sonck.gplaymusic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ZeppaEllaPlaylistCreatorIntegrationTest {

    @Autowired
    private ZeppaEllaPlaylistCreator playlistCreator;


    @Test
    public void refreshPlaylist() {
        playlistCreator.refreshPlaylist();
    }
}