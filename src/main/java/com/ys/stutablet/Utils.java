package com.ys.stutablet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static void addDir(String s) throws IOException {

        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[])field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length+1];
            System.arraycopy(paths,0,tmp,0,paths.length);
            tmp[paths.length] = s;
            field.set(null,tmp);
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }

    public static void loadWgssSTULibrary() {
        try {
            Utils.addDir(System.getProperty("user.home"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        try {
            System.loadLibrary("wgssSTU");
        } catch (UnsatisfiedLinkError e) {
            String name = "wgssSTU.dll";
            Path path = FileSystems.getDefault().getPath(System.getProperty("user.home"), name);
            log.debug("Path to STU library: {}",path.toString());

            try (InputStream input = Main.class.getResourceAsStream("/"+name)) {
                if (input == null) {
                    log.error("Not found resource wgssSTU.dll");
                    throw new FileNotFoundException("Не найден ресурс wgssSTU.dll");
                }
                Files.copy(input, path);
                log.debug("Path for coping of STU library: {}", path.toString());
                System.loadLibrary("wgssSTU");
            }
            catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }
        }

    }






}
