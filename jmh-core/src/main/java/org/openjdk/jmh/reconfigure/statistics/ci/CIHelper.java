package org.openjdk.jmh.reconfigure.statistics.ci;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

public class CIHelper {
    private static CIHelper instance;
    private File tempFile;

    private CIHelper() {
        copyExecutableToTmpFolder();
    }

    public static CIHelper getInstance() {
        if (CIHelper.instance == null) {
            CIHelper.instance = new CIHelper();
        }
        return CIHelper.instance;
    }

    public String getPath() {
        return tempFile.getAbsolutePath();
    }

    private void copyExecutableToTmpFolder() {
        String executableName = executableName();
        tempFile = Paths.get(System.getProperty("java.io.tmpdir"), executableName).toFile();

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(executableName());
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            if (resource.getProtocol().equals("jar")) {
                copyExecutableToTmpFolderJar(resource);
            } else {
                copyExecutableToTmpFolderFile(resource);

            }
        }
    }

    private void copyExecutableToTmpFolderFile(URL resource) {
//        try {
//            File executable = new File(resource.getFile());
//            FileUtils.copyFile(executable, tempFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void copyExecutableToTmpFolderJar(URL resource) {
        try {
            InputStream in = resource.openStream();
            FileUtils.copyInputStreamToFile(in, tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    private String executableName() {
        return isWindows() ? "pa.exe" : "pa";
    }
}
