package uk.co.itstherules;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class FileTestHelper {

    private FileTestHelper() {}

    public static void deleteContentsOf(File rootDir) {
        final File[] files = rootDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteContentsOf(file);
                }
                assertThat(file.delete(), is(true));
            }
        }
    }

    public static String readFileToString(File file) throws IOException {
        final FileReader reader = new FileReader(file);
        StringBuilder b = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            b.append((char) c);
        }
        reader.close();
        return b.toString();
    }

    public static void writeFileFromString(File file, String document) throws IOException {
        final FileWriter writer = new FileWriter(file);
        writer.write(document);
        writer.close();
    }

}
