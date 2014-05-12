package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;

/**
 * NOT an ENTITY: only as string blob, used in Dependency Build
 */
public class Change implements Comparable<Change> {
    private String author;
    private String revision;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    @Override
    public int compareTo(@NotNull Change o) {
        if (this == o) return 0;
        return revision.compareTo(o.getRevision());
    }

    @Override
    public String toString() {
        return "author = " + author + "; revision = " + revision;
    }
}
