package com.chriniko.lunatech.movies.service.data;

import com.chriniko.lunatech.movies.error.ProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Component
public class FileOperations {

    @Value("${output.directory}")
    private String outputFileDir;

    String unzip(String resourceName, String unzippedFileName) {

        try (GZIPInputStream gzis =
                     new GZIPInputStream(this.getClass().getClassLoader().getResourceAsStream(resourceName));
             FileOutputStream out =
                     new FileOutputStream(unzippedFileName)) {

            byte[] buffer = new byte[1024];

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            return unzippedFileName;

        } catch (IOException error) {
            throw new ProcessingException("FileOperations#unzip, error: " + error.getMessage(), error);
        }
    }


}
