package hr.fer.oprpp2.servlets.glasanje;

import hr.fer.oprpp2.models.Band;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Utility class for loading band definitions and votes from files.
 */
public class GlasanjeUtil {

    /**
     * Loads band definitions from a file and returns a map of band IDs with their definitions.
     *
     * @param req HTTP request
     * @return map of band IDs with their definitions
     */
    public static Map<Integer, Band> loadBandDefs(HttpServletRequest req) {
        Path defFilePath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt"));
        Map<Integer, Band> bendovi = new HashMap<>();
        try {
            Files.readAllLines(defFilePath).forEach(line -> {
                String[] parts = line.split("\t");
                bendovi.put(Integer.parseInt(parts[0]), new Band(Integer.parseInt(parts[0]), parts[1], parts[2]));
            });
        } catch (IOException ignored) {
        }
        return bendovi;
    }

    /**
     * Loads band votes from a file and returns a map of band IDs with their votes.
     *
     * @param req HTTP request
     * @return map of band IDs with their votes
     */
    public static Map<Integer, Integer> loadBandVotes(HttpServletRequest req) {
        Path rezFilePath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt"));
        Map<Integer, Integer> votes = new TreeMap<>();
        try {
            Files.readAllLines(rezFilePath).forEach(line -> {
                String[] parts = line.split("\t");
                votes.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            });
        } catch (IOException ignored) {
        }
        return votes;
    }

    /**
     * Loads band definitions and votes from files and returns a map of bands with their votes.
     *
     * @param req HTTP request
     * @return map of bands with their votes
     */
    public static Map<Integer, Band> loadBandsWithVotes(HttpServletRequest req) {
        Map<Integer, Band> bands = loadBandDefs(req);
        Map<Integer, Integer> votes = loadBandVotes(req);
        votes.forEach((id, vote) -> bands.get(id).setVotes(vote));
        return bands;
    }
}
