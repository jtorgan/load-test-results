package load_test_service.storage.binding;

import jetbrains.exodus.database.ByteIterable;
import jetbrains.exodus.database.ByteIterator;
import jetbrains.exodus.database.impl.LightOutputStream;
import jetbrains.exodus.database.impl.bindings.ComparableBinding;
import jetbrains.exodus.database.impl.bindings.IntegerBinding;
import jetbrains.exodus.database.impl.iterate.ArrayByteIterable;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Convert collection of objects to InputStream/ByteIterable and vice versa using provided binding
 * note: return collection based on ArrayList
 */
public class CollectionConverter {
    private static final CollectionConverter BINDING = new CollectionConverter();

    private  <T> Collection<T> readCollection(@NotNull ByteIterator it, @NotNull ComparableBinding binding) {
        int size = IntegerBinding.readCompressed(it);
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            list.add((T) binding.readObject(it));
        return list;
    }
    private  <T extends Comparable> LightOutputStream writeCollection(@NotNull ComparableBinding binding, @NotNull Collection<T> list) {
        LightOutputStream output = new LightOutputStream();
        IntegerBinding.writeCompressed(output, list.size());
        if (list.size() > 0) {
            for (Comparable comparable : list)
                binding.writeObject(output, comparable);
        }
        return output;
    }


    public static <T extends Comparable> InputStream toInputStream(@NotNull ComparableBinding binding, @NotNull Collection<T> list) {
        LightOutputStream outputStream = BINDING.writeCollection(binding, list);
        return new ByteArrayInputStream(outputStream.getBufferBytes());
    }
    public static <T> Collection<T> fromInputStream(@NotNull InputStream inputStream, @NotNull ComparableBinding binding) {
        ByteIterable bytes;
        try {
            bytes = new ArrayByteIterable(IOUtils.toByteArray(inputStream));
            return BINDING.readCollection(bytes.iterator(), binding);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public static <T extends Comparable> ByteIterable toByteArray(@NotNull ComparableBinding binding, @NotNull Collection<T> list) {
        LightOutputStream outputStream = BINDING.writeCollection(binding, list);
        return outputStream.asArrayByteIterable();
    }
    public static <T> Collection<T> fromByteArray(@NotNull ByteIterable bytes, @NotNull ComparableBinding binding) {
        return BINDING.readCollection(bytes.iterator(), binding);
    }

}
