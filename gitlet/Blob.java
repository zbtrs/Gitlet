package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    private String sha1, contents;

    public Blob(File obj) {
        contents = Utils.readContentsAsString(obj);
        sha1 = Utils.sha1(contents);
    }

    public String SHA1() {
        return sha1;
    }

    public String contents() {
        return contents;
    }

}
