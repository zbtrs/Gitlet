package gitlet;

public class QueueNode {
    private Commit commit;
    private int depth;

    public QueueNode(Commit commit,int depth) {
        this.commit = commit;
        this.depth = depth;
    }

    public Commit commit() {
        return commit;
    }

    public int depth() {
        return depth;
    }

}
