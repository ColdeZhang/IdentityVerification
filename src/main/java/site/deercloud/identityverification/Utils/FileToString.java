package site.deercloud.identityverification.Utils;

import java.io.File;
import java.io.FileReader;

public class FileToString {

    public static String Read(File file){
        try (FileReader fileReader = new FileReader(file)) {
            char[] chars = new char[(int) file.length()];
            if (fileReader.read(chars) != -1) {
                return new String(chars);
            }
            return new String(chars);
        } catch (Exception e) {
            MyLogger.debug(e);
            return null;
        }
    }

}
