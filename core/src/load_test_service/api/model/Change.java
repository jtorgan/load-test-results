package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;

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
}
