package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;

public class Config {
    public HashMap<String, File> commit2file = new HashMap<>();
    public HashMap<String,File> branch2commit = new HashMap<>();
    public HashSet<String> addcaches = new HashSet<>();
    public HashSet<String> removecaches = new HashSet<>();


    private final File HEADfile = join(Repository.GITLET_DIR,"HEAD");
    private final File commits = join(Repository.GITLET_DIR,"commits");
    private final File branchs = join(Repository.GITLET_DIR,"branchs");
    private final File addcachesfile = join(Repository.GITLET_DIR,"addcachesfile");
    private final File removecachefile = join(Repository.GITLET_DIR,"removecachefile");
    private final File nowbranch = join(Repository.GITLET_DIR,"nowbranch");
    public String HEAD = "",branch = "";

    public void load() {
        commit2file = Utils.readObject(commits,HashMap.class);
        branch2commit = Utils.readObject(branchs,HashMap.class);
        addcaches = Utils.readObject(addcachesfile,HashSet.class);
        removecaches = Utils.readObject(removecachefile,HashSet.class);
        HEAD = Utils.readContentsAsString(HEADfile);
        branch = Utils.readContentsAsString(nowbranch);
    }

    public void store() {
        Utils.writeObject(commits, (Serializable) commit2file);
        Utils.writeObject(branchs,(Serializable) branch2commit);
        Utils.writeObject(addcachesfile, (Serializable) addcaches);
        Utils.writeObject(removecachefile,(Serializable) removecaches);
        Utils.writeContents(HEADfile,HEAD);
        Utils.writeContents(nowbranch,branch);
    }
    public void init() {
        createfile(HEADfile);
        createfile(commits);
        createfile(branchs);
        createfile(addcachesfile);
        createfile(removecachefile);
        createfile(nowbranch);
        store();
    }

    public void remove_addcache(String filename) {
        if (addcaches.contains(filename)) {
            addcaches.remove(filename);
        }
    }

    public void add_addcache(String filename) {
        if (addcaches.contains(filename)) {
            return;
        }
        addcaches.add(filename);
    }

    public void updatecommit(Commit newcommit,File newcommitfile) {
        commit2file.put(newcommit.SHA1(),newcommitfile);
    }

    public void updatebranch(String nowbranch,File newcommitfile) {
        branch2commit.put(nowbranch,newcommitfile);
    }

    public File getbranchcommit(String branchname) {
        return branch2commit.get(branchname);
    }

}
