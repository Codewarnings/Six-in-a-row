public final class Node{//用于存储已落子坐标(x,y)
    int x;
    int y;
    Node(){
    }
    Node(int x,int y){
        this.x=x;
        this.y=y;
    }
    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}