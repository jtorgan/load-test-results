package load_test_service.statistic.readers;

import load_test_service.api.exeptions.FileFormatException;

import java.io.*;

public abstract class FileReader {

    public void processFile(InputStream artifact) throws FileFormatException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(artifact));

            reader.readLine();// skip titles
            String line;
            while (reader.ready() && !(line = reader.readLine()).isEmpty()) {
                processLine(line);
            }
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    protected abstract void processLine(String line) throws FileFormatException;
}





