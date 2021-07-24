public final class NodeFlag implements Comparable<NodeFlag>{
    int x;
    int y;
    int flag;

    public NodeFlag(int x, int y, int flag) {
        this.x = x;
        this.y = y;
        this.flag = flag;
    }
    @Override
    public int compareTo(NodeFlag o) {
        return o.flag-flag;
    }
}
