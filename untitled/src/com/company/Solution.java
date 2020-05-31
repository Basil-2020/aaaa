package com.company;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllLines;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;

/*
Переопределение сериализации в потоке
*/
public class Solution implements Serializable, AutoCloseable {
    private transient FileOutputStream stream;
    private final String fileName;
    private final static String TEST_DATA ="testStringObject";

    public Solution(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        this.stream = new FileOutputStream(fileName);
    }

    public void writeObject(String string) throws IOException {
        stream.write(string.getBytes());
        stream.write("\n".getBytes());
        stream.flush();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.stream = new FileOutputStream(fileName,true);
    }

    @Override
    public void close() throws Exception {
        System.out.println("Closing everything!");
        stream.close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String outputFile = "outputFile.txt";

        Solution solution = new Solution(outputFile);
        solution.writeObject(TEST_DATA);

        File tmpFile = File.createTempFile("tmpFile", ".tmp");

        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(tmpFile));
        os.writeObject(solution);

        ObjectInputStream is = new ObjectInputStream(new FileInputStream(tmpFile));

        Solution deserializedSolution = (Solution) is.readObject();
        deserializedSolution.writeObject(TEST_DATA);

        List<String> strings = readAllLines(Paths.get(outputFile), UTF_8);
        System.out.println(TEST_DATA.equals(strings.get(0)) ? "OK" : "Файл не содержит данные из п.2");
        System.out.println(TEST_DATA.equals(strings.get(1)) ? "OK" : "Файл не содержит данные из п.5");
    }
}
