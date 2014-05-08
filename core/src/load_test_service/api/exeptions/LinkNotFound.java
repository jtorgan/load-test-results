package load_test_service.api.exeptions;

import jetbrains.exodus.database.Entity;

public class LinkNotFound extends Exception{
    public LinkNotFound(String linkName, Entity which) {
        super("Link not found:\nlinkName=" + linkName + ";\nentityType=" + which.getType());
    }
}
