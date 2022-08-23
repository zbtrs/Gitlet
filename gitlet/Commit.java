package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author zbtrs
 */
public class Commit implements Serializable{
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String sha1;
    private commitcontents obj;
    private Set<String> blobnames;
    private Map<String, String> blobsha1;

    public boolean equalinitial() {
        if (obj.parentcommit.equals("")) {
            return true;
        }
        return false;
    }


    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }
    public void print() {
        System.out.println("===");
        System.out.format("commit %s\n", sha1);
        if (!obj.secondparentcommit.equals("")) {
            System.out.format("Merge: %s %s\n", obj.parentcommit.substring(0,7),obj.secondparentcommit.substring(0,7));
        }
        System.out.format("Date: %s\n",dateToTimeStamp(obj.createDate));
        System.out.format("%s\n",obj.message);
        System.out.println();
    }

    public String parent() {
        return obj.parentcommit;
    }

    public void removeblob(String item, Blob cacheblob) {
        blobnames.remove(item);
        obj.blobs.remove(cacheblob);
    }

    public String message() {
        return obj.message;
    }

    public String parent2() {
        return obj.secondparentcommit;
    }

    public void setmerge(String branch) {
        obj.secondparentcommit = branch;
    }

    private class commitcontents implements Serializable {
        private String message,parentcommit,secondparentcommit;
        private Date createDate;
        private Set<Blob> blobs;

        public void update(Blob oldblob,Blob newblob) {
            blobs.remove(oldblob);
            blobs.add(newblob);
        }

        public String getsha1() {
            List<Object> sha1list = new ArrayList<>();
            sha1list.add(message);
            sha1list.add(parentcommit);
            sha1list.add(createDate.toString());
            for (Blob item : blobs) {
                sha1list.add(item.SHA1());
            }
            return sha1(sha1list);
        }
    }

    public Commit(String message,Date date) {
        blobnames = new HashSet<>();
        blobsha1 = new HashMap<>();
        obj = new commitcontents();
        obj.message = message;
        obj.parentcommit = "";
        obj.secondparentcommit = "";
        obj.createDate = date;
        obj.blobs = new HashSet<>();
        sha1 = obj.getsha1();
    }

    public Commit(String parentid,File parentcommit,String message,Date date) {
        Commit temp = Utils.readObject(parentcommit,Commit.class);
        this.obj = temp.obj;;
        this.sha1 = temp.sha1;
        this.blobnames = temp.blobnames;
        this.blobsha1 = temp.blobsha1;
        obj.message = message;
        obj.createDate = date;
        obj.parentcommit = parentid;
        obj.secondparentcommit = "";
    }

    public Set<Blob> blobs() {
        return obj.blobs;
    }

    public Set<String> blobnames() {
        return blobnames;
    }

    public boolean containblob(String filename,String contents) {
        if (contents.equals(blobsha1.get(filename))) {
            return true;
        }
        return false;
    }
    public boolean contain(String item) {
        return blobnames.contains(item);
    }

    public void additem(String item,Blob newblob) {
        blobnames.add(item);
        obj.blobs.add(newblob);
        blobsha1.put(item,newblob.SHA1());
    }

    public String getblobsha1(String blobname) {
        return blobsha1.get(blobname);
    }

    public void updateblob(String name,Blob newblob) {
        //通过blob的名字得到对应的SHA1
        String oldblobsha1 = blobsha1.get(name);
        File oldblobfile = join(Repository.BLOBS_DIR,oldblobsha1);
        //从BLOBS_DIR中读取原来blob
        Blob oldblob = new Blob(oldblobfile);
        obj.update(oldblob,newblob);
        blobsha1.put(name,newblob.SHA1());
    }

    public void update() {
        sha1 = obj.getsha1();
    }

    public String SHA1() {
        return sha1;
    }

}
