package uk.co.itstherules.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardOpenOption.*;

public final class FileDataStore implements DataStore {

    private final File baseDirectory;

    public FileDataStore(String path) {
        baseDirectory = new File(path);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }
        for (Section section : Section.values()) {
            new File(baseDirectory, sectionName(section)).mkdirs();
        }

    }

    @Override public void store(Section section, String uuid, String document) {
        try {
            FileSystem fileSystem = FileSystems.getDefault();
            Path path = fileSystem.getPath(new File(baseDirectory + "/" + sectionName(section), uuid).getAbsolutePath());
            FileChannel outChannel = FileChannel.open(path, EnumSet.of(CREATE, TRUNCATE_EXISTING, WRITE));
            ByteBuffer buffer = ByteBuffer.wrap(document.getBytes("utf-8"));
            outChannel.write(buffer);
            outChannel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String sectionName(Section section) {return section.name().toLowerCase();}

    @Override public String retrieve(Section section, String uuid) {
        StringBuilder b = new StringBuilder();
        try {
            FileInputStream inputStream = new FileInputStream(new File(baseDirectory + "/" + sectionName(section), uuid));
            FileChannel fileChannel = inputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int bytes = fileChannel.read(byteBuffer);
            while (bytes != -1) {
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    b.append((char) byteBuffer.get());
                }
                byteBuffer.clear();
                bytes = fileChannel.read(byteBuffer);
            }
            inputStream.close();
        } catch (IOException e) {
            return "";
        }
        return b.toString();
    }

    @Override public Map<String, String> retrieveAll(Section section) {
        Map<String, String> reply = new HashMap<String, String>();
        File root = new File(baseDirectory + "/" + sectionName(section));
        final File[] files = root.listFiles(new FileFilter() {
            @Override public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        for (File file : files) {
            final String uuid = file.getName();
            final String document = retrieve(section, uuid);
            reply.put(uuid, document);
        }
        return reply;
    }
}
