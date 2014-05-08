package load_test_service.api.exeptions;

import java.util.Arrays;

/**
 * Created by Yuliya.Torhan on 5/8/14.
 */
public class EntityNotFound extends Exception {
    public EntityNotFound(String type, String... properties) {
        super("Entity not found! Type:" + type + "; properties: " + Arrays.toString(properties));
    }
}
