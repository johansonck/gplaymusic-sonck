package be.sonck.gplaymusic;

import com.github.felixgail.gplaymusic.model.PlaylistEntry;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class PlaylistEntryUtil {

    private PlaylistEntryUtil() {
    }

    public static LocalDateTime getCreationTime(PlaylistEntry playlistEntry, ZoneId zoneId) {
        String creationTimestamp = playlistEntry.getCreationTiestamp();
        if (creationTimestamp == null) return null;

        Long epochMicroseconds = Long.valueOf(creationTimestamp);
        Long epochMilliseconds = epochMicroseconds / 1000;
        Instant instant = Instant.ofEpochMilli(epochMilliseconds);

        return LocalDateTime.ofInstant(instant, zoneId);
    }
}
