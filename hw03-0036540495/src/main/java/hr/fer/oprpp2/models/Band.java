package hr.fer.oprpp2.models;

import java.util.Objects;

/**
 * Represents a band with its id, name, link and number of votes.
 * The id is unique for each band.
 */
public final class Band {
    private final int id;
    private final String name;
    private final String link;
    private int votes;

    public Band(int id, String name, String link, int votes) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.votes = votes;
    }

    public Band(int id, String name, String link) {
        this(id, name, link, 0);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Band) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Band[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "link=" + link + ", " +
                "votes=" + votes + ']';
    }

}
