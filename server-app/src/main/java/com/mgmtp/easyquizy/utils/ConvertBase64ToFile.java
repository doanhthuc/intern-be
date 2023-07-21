package com.mgmtp.easyquizy.utils;

import com.mgmtp.easyquizy.dto.kahoot.UploadImageToKahootResultDto;
import com.mgmtp.easyquizy.exception.file.ConvertFileException;
import com.mgmtp.easyquizy.exception.file.DeleteFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Base64;
import java.util.Set;

@Slf4j
public class ConvertBase64ToFile {
    private ConvertBase64ToFile() {
    }
    public static File convert(String base64Image) {
        // Exclude data:image/jpeg;base64 in base64Photo string from database
        base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
        byte[] photoBytes = Base64.getDecoder().decode(base64Image);
        // Convert photoBytes to File
        File imageFile;
        try {
            if(SystemUtils.IS_OS_UNIX) {
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
                imageFile = Files.createTempFile("photo", ".jpg", attr).toFile();
            }
            else {
                imageFile = File.createTempFile("photo", ".jpg", new File(System.getProperty("java.io.tmpdir")));
                boolean isReadable = imageFile.setReadable(true, true);
                boolean isWriteable = imageFile.setWritable(true, true);
                if (!isReadable || !isWriteable) {
                    throw new ConvertFileException("Error when convert base64 to file: " + "Cannot set readable and writeable for file");
                }
            }
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(photoBytes);
            }
        } catch (IOException e) {
            throw new ConvertFileException("Error when convert base64 to file: " + e.getMessage());
        }
        return imageFile;
    }

    public static UploadImageToKahootResultDto convertAndUploadImage(String base64Image, String targetUrl, String token) {
        // Upload imageFile to targetUrl
        File imageFile = convert(base64Image);
        UploadImageToKahootResultDto uploadResult = new RestClient().setBearerToken(token)
                .setMultipartRequestBody("f", imageFile)
                .setUrl(targetUrl).setMethod("POST").call(UploadImageToKahootResultDto.class);
        // Delete imageFile after upload
        try {
            Files.delete(imageFile.toPath());
            return uploadResult;
        } catch (Exception e) {
            throw new DeleteFileException("Error when delete image: " + e.getMessage());
        }
    }
}
