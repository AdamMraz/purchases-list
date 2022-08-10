package ru.adamdev.purchases.list.util;

import org.apache.log4j.Logger;
import ru.adamdev.purchases.list.exception.PurchasesListException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_CANNOT_READ_FILE;
import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_CANNOT_SAVE;
import static ru.adamdev.purchases.list.constant.ErrorConstants.TYPE_ERROR;

public class FileUtil {

    private static final Logger LOG = Logger.getLogger(FileUtil.class);

    public static <T> void writeToFile(T output, File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            String json = Optional.ofNullable(JsonUtil.serialize(output)).orElseThrow(() -> new IOException(MESSAGE_CANNOT_SAVE));
            outputStream.write(json.getBytes());
        } catch (IOException e) {
            LOG.warn(MESSAGE_CANNOT_SAVE);
        }
    }

    public static String readFile(File file) throws PurchasesListException {
        try (InputStream inputStream = new FileInputStream(file.getName())) {
            return readFile(inputStream);
        } catch (IOException e) {
            throw new PurchasesListException(TYPE_ERROR, MESSAGE_CANNOT_READ_FILE, e);
        }
    }

    private static String readFile(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(" "));
        }
    }
}
