package be.sonck.gplaymusic;

import com.github.felixgail.gplaymusic.api.GPlayMusic;
import com.github.felixgail.gplaymusic.model.Playlist;
import com.github.felixgail.gplaymusic.model.PlaylistEntry;
import com.github.felixgail.gplaymusic.model.Track;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ZeppaEllaPlaylistCreator {

    private GPlayMusic gPlayMusic;

    @Autowired
    public ZeppaEllaPlaylistCreator(GPlayMusic gPlayMusic) {
        this.gPlayMusic = gPlayMusic;
    }

    public void refreshPlaylist() {
        try {
            String playlistName = "Nieuwe Z&E";

            Playlist playlist = getPlaylist(playlistName).orElseThrow(() -> new IllegalArgumentException("Playlist " + playlistName + " not found"));

            clearPlaylist(playlist);

            addTracks(playlist, getLatestTracks());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTracks(Playlist playlist, List<Track> latestTracks) throws IOException {
        gPlayMusic.getPlaylistApi().addTracksToPlaylist(playlist, latestTracks);

        log.info("added new tracks to playlist {}", playlist.getName());
    }

    private void clearPlaylist(Playlist playlist) throws IOException {
        List<PlaylistEntry> playlistEntries = playlist.getContents(0);

        playlist.removeEntries(playlistEntries);
        gPlayMusic.getPlaylistEntryApi().deletePlaylistEntries(playlistEntries);

        log.info("cleared playlist {}", playlist.getName());
    }

    private List<Track> getLatestTracks() {
        List<Track> latestTracks = new ArrayList<>();

        addLatestTracks(latestTracks, "Ella");
        addLatestTracks(latestTracks, "Zeppa");

        latestTracks.sort(Comparator.comparing(Track::getTitle));

        latestTracks.forEach(track -> log.debug("found track {}",
                ToStringBuilder.reflectionToString(track, ToStringStyle.JSON_STYLE)));

        return latestTracks;
    }

    private void addLatestTracks(List<Track> latestTracks, String playlistName) {
        getLatestEntries(playlistName).stream()
                .filter(track -> !containsMatchingTrack(latestTracks, track))
                .forEach(latestTracks::add);
    }

    private boolean containsMatchingTrack(List<Track> tracks, Track track) {
        return tracks.stream().anyMatch(t -> Objects.equals(track.getTitle(), t.getTitle()) && Objects.equals(track.getArtist(), t.getArtist()));
    }

    private List<Track> getLatestEntries(String playlistName) {
        Optional<Playlist> optionalPlaylist = getPlaylist(playlistName);
        if (!optionalPlaylist.isPresent()) return Collections.emptyList();

        return getLatestEntries(optionalPlaylist.get())
                .stream()
                .map(this::getTrack)
                .collect(Collectors.toList());
    }

    private List<PlaylistEntry> getLatestEntries(Playlist playlist) {
        List<PlaylistEntry> playlistEntries = getEntries(playlist);
        int size = playlistEntries.size();

        return playlistEntries.subList(size - 20, size);
    }

    private Track getTrack(PlaylistEntry entry) {
        try {
            return entry.getTrack();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Playlist> getPlaylist(String name) {
        return listPlaylists().stream()
                .filter(playlist -> Objects.equals(name, playlist.getName()))
                .findFirst();
    }

    private List<PlaylistEntry> getEntries(Playlist playlist) {
        try {
            return gPlayMusic.getPlaylistApi().getContents(playlist, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Playlist> listPlaylists() {
        try {
            return gPlayMusic.getPlaylistApi().listPlaylists();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
