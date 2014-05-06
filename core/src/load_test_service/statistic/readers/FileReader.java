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





    /**
     * Helper to log failed items in temp files
     */
    protected final static class FileHelper {

        private static void appendLineToFile(String fileName, String line) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(fileName, true));
                out.write(line + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private static String getFileContent(String fileName) {
            BufferedReader in = null;
            StringBuilder builder = new StringBuilder();
            try {
                in = new BufferedReader(new java.io.FileReader(fileName));
                while (in.ready()) {
                    builder.append(in.readLine()).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }
    }
}





