package load_test_service.storage.binding;

import load_test_service.api.model.Change;
import jetbrains.exodus.database.ByteIterator;
import jetbrains.exodus.database.impl.LightOutputStream;
import jetbrains.exodus.database.impl.bindings.ComparableBinding;
import jetbrains.exodus.database.impl.iterate.IterableUtils;
import org.jetbrains.annotations.NotNull;

public class ChangeBinding  extends ComparableBinding {
    public static final ChangeBinding BINDING = new ChangeBinding();

    @Override
    public Comparable readObject(@NotNull ByteIterator it) {
        Change change = new Change();
        change.setAuthor(IterableUtils.readString(it));
        change.setRevision(IterableUtils.readString(it));
        return change;
    }

    @Override
    public void writeObject(@NotNull LightOutputStream output, @NotNull Comparable object) {
        Change change = (Change) object;
        output.writeString(change.getAuthor());
        output.writeString(change.getRevision());
    }
}