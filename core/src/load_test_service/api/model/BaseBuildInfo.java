package load_test_service.api.model;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseBuildInfo implements Comparable<BaseBuildInfo> {
    private static final DateFormat TC_Date_Format = new SimpleDateFormat("yyyyMMdd'T'HHmmsszzz");
    private static final DateFormat USER_Date_Format = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss");

    protected final BuildID id;

    protected BaseBuildInfo(@NotNull BuildID id) {
        this.id = id;
    }

    protected String buildNumber;
    protected String status;
    protected String finishDate;


    public BuildID getID() {
        return id;
    }

    public String getBuildNumber() {
        return buildNumber;
    }
    public String getStatus() {
        return status;
    }
    public String getFinishDate() {
        return finishDate;
    }

    @NotNull
    public String getFinishFormattedDate() {
        try {
            Date result = TC_Date_Format.parse(finishDate);
            return USER_Date_Format.format(result);
        } catch (ParseException e) {
            e.printStackTrace();
            return "NULL: parse exception";
        }
    }

    public void setBuildNumber(@NotNull final String buildNumber) {
        this.buildNumber = buildNumber;
    }
    public void setStatus(@NotNull final String status) {
        this.status = status;
    }
    public void setFinishDate(@NotNull final String finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public int compareTo(@NotNull BaseBuildInfo o) {
        return id.compareTo(o.getID());
    }

    @Override
    public String toString() {
        return "BaseBuildInfo: [" + id + "; bn = " + buildNumber + "; status = " + status + "; finishDate = " + finishDate + "]\n";
    }

}
