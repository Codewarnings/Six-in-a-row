import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class ChessMap {
    public static final int EMPTY=0;
    public static final int WHITE=1;
    public static final int BLACK=2;
    public static long cnt=0;
    public ArrayList<Node> nodes=new ArrayList<>(400);
    public int[][] chessmap =new int[20][20];
    public int[][] chessMapCount=new int[20][20];
    public boolean aiIsStop=false;
    private static final int[][] dxdy=new int[][]{{-1,-1},{-1,0},{-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};
    private static final long[] value=new long[9];
    private final long[] lineEvalueSum=new long[20];//Use Index:[1,19]
    private final long[] cowEvalueSum=new long[20];//Use Index:[1,19]
    private final long[] leftEvlueSum=new long[38];
    //Use Index:5<x+y-1<33 besides [1,5]+[33,37] is always ZERO
    private final long[] rightEvalueSum=new long[38];
    private static final long INF=Long.MAX_VALUE>>1;
    private int AiColor;
    //Use Index:[5,33] besides [1,5]+[33,37] is always ZERO
    //when x>=y Index:20-(x-y+1)
    //when x<y  Index:38-(20-(y-x+1))
    private static final String[][] whiteChessType=new String[][]{
            {"0111100","0011110"}//活四
            ,{"0111110","10111100","00111101"}//活五
            ,{"2111110","2111101","2111011","2110111","2101111",
            "2011111","0111112","1011112","1101112","1110112",
            "1111012","1111102"}//眠五
            ,{"2011110","2101110","2110110","2111010","2111100",
            "2011101","2101101","2110101","2111001","2111100",
            "2011011","2101011","2110011","2111001","2111010",
            "2010111","2100111","2110011","2110101","2110110",
            "20011112","2100111","2101011","2101101","2101110",
            "2001111","2010111","2011011","2011101","2011110",
            "0111102","0111012","0110112","0101112","0011112",
            "1011102","1011012","1010112","1001112","0011112",
            "1101102","1101012","1100112","1001112","0101112",
            "1110102","1110012","1100112","1010112","0110112",
            "21111002","1110012","1101012","1011012","0111012",
            "1111002","1110102","1101102","1011102","0111102"}//眠四
            ,{"0011100","0001110","0111000","0101100","0011010",
            "0010110","0110100"}//活三
            ,{"2011100","0011102","2001110","0111002","2101010",
            "0101012"}//朦胧三
            ,{"0001112","2111000","2101100","2100110","2110100",
            "2110010"}//眠三
            ,{"011000","001100","000110","010100","001010","010010"}//活二
            ,{"2110000","0000112","2101000","0001012","2100100",
            "0010012","010001","100010","2011000","0001102",
            "2100010","0100012","2010100","0010102","2010010",
            "0100102","2010001","1000102"}//眠二
    };
    private static final String[][] blackChessType=new String[][]{
            {"0222200","0022220"}//活四
            ,{"0222220","20222200","00222202"}//活五
            ,{"1222220","1222202","1222022","1220222","1202222", "1022222"
            ,"0222221","2022221","2202221","2220221","2222021","2222201"}//眠五
            ,{"1022220","1202220","1220220","1222020","1222200"
            ,"1022202","1202202","1220202","1222002","1222200"
            ,"1022022","1202022","1220022","1222002","1222020"
            ,"1020222","1200222","1220022","1220202","1220220"
            ,"10022221","1200222","1202022","1202202","1202220"
            ,"1002222","1020222","1022022","1022202","1022220",
            "0222201","0222021","0220221","0202221","0022221",
            "2022201","2022021","2020221","2002221","0022221",
            "2202201","2202021","2200221","2002221","0202221",
            "2220201","2220021","2200221","2020221","0220221",
            "12222001","2220021","2202021","2022021","0222021",
            "2222001","2220201","2202201","2022201","0222201"}//眠四
            ,{"0022200","0002220","0222000","0202200","0022020","0020220","0220200"}//活三
            ,{"1022200","0022201","1002220","0222001","1202020","0202021"}//朦胧三
            ,{"0002221","1222000","1202200","1200220","1220200","1220020"}//眠三
            ,{"022000","002200","000220","020200","002020","020020",}//活二
            ,{"1220000","0000221","1202000","0002021","1200200"
            , "0020021","020002","200020","1022000","0002201","1200020",
            "0200021","1020200","0020201","1020020","0200201","1020002","2000201"}//眠二
    };
    static {
        long HUOSI=5000000000L;
        value[0]=HUOSI;
        long HUOWU=1000000000L;
        value[1]=HUOWU;
        long MIANWU=200000000L;
        value[2]=MIANWU;
        long MIANSI=80000000L;
        value[3]=MIANSI;
        long HUOSAN=10000000L;
        value[4]=HUOSAN;
        long MENGLONGSAN=1000000L;
        value[5]=MENGLONGSAN;
        long MIANSAN=80000L;
        value[6]=MIANSAN;
        long HUOER=10000L;
        value[7]=HUOER;
        long MIANER=2000L;
        value[8]=MIANER;
    }
    private ArrayList<NodeFlag> allFlag(){
        ArrayList<NodeFlag> flags=new ArrayList<>(400);
        Node a= nodes.get(nodes.size()-1);
        Node b=null;
        if(nodes.size()>1)
            b=nodes.get(nodes.size()-2);
        for(int i=1;i<=19;i++){
            for(int j=1;j<=19;j++){
                if(chessmap[i][j]==EMPTY&&chessMapCount[i][j]!=0){
                    if(Math.abs(a.x-i)<=2&&Math.abs(a.y-j)<=2)
                        flags.add(new NodeFlag(i,j,chessMapCount[i][j]+50));
                    else if(b!=null&&Math.abs(b.x-i)<=2&&Math.abs(b.y-j)<=2)
                        flags.add(new NodeFlag(i,j,chessMapCount[i][j]+50));
                    else
                        flags.add(new NodeFlag(i,j,chessMapCount[i][j]));
                }
            }
        }
        Collections.sort(flags);
        //System.err.println("Size:"+flags.size());
        while(flags.size()>90){
            flags.remove(flags.size()-1);
        }
        return flags;
    }
    public void updataFlag(int x,int y,int cnt){
        for(int i=x-3;i<=x+3;i++){
            for(int j=y-3;j<=y+3;j++){
                if(!isInChessRange(i,j))
                    continue;
               chessMapCount[i][j]+=cnt*(4-Math.max(Math.abs(x-i),Math.abs(y-j)));
            }
        }
        /*for(int i=1;i<=19;i++){
            for(int j=1;j<=19;j++){
                if(chessmap[i][j]==EMPTY)
                    System.err.print(chessMapCount[i][j]+" ");
                else
                    System.err.print("* ");
            }
            System.err.println();
        }
        System.err.println();*/
    }
    boolean canDrop(int x,int y){
        return chessmap[x][y]==EMPTY ;
    }
    public int lastChessColor(){//计算上次落子颜色
        if(nodes.size()<=1)
            return BLACK;
        return (nodes.size()/2&1)==1?WHITE:BLACK;
    }
    public int nextChessColor(){//计算下次落子颜色
        if(nodes.size()==0)
            return BLACK;
        return ((nodes.size()+1)/2&1)==1?WHITE:BLACK;
    }
    public int thisChessColor(int i){//第i个棋子颜色
        if(i<=1)
            return BLACK;
        return (i/2&1)==1?WHITE:BLACK;
    }
    public boolean isInChessRange(int x, int y){
        return x >= 1 && x <= 19 && y >= 1 && y <= 19;
    }
    public boolean isDropSuccess(int x,int y){
        for (int i=0;i<4;i++) {
            if(continuousSameColor(x,y,dxdy[i][0],dxdy[i][1])+
                    continuousSameColor(x,y,-dxdy[i][0],-dxdy[i][1])+1>=6){
                return true;
            }
        }
        return false;
    }
    public int continuousSameColor(int x,int y,int dx,int dy){
        int a,b,cnt=0;
        a=x+dx;
        b=y+dy;
        while(isInChessRange(a,b)&&chessmap[a][b]==chessmap[x][y]){
            ++cnt;
            a+=dx;
            b+=dy;
        }
        return cnt;
    }
    private long getLineEvalue(int x,int y,int mainChessColor){
        StringBuilder s=new StringBuilder();
        int start=1;
        int cnt=0;
        while(start<=19){
            if(chessmap[x][start]!=EMPTY)
                break;
            ++start;
            ++cnt;
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        int end=19;
        cnt=0;
        while(end>=1){
            if(chessmap[x][end]!=EMPTY)
                break;
            --end;
            ++cnt;
        }
        for(int j=start;j<=end;j++){
            if(chessmap[x][j]==WHITE)
                s.append('1');
            else if(chessmap[x][j]==BLACK)
                s.append('2');
            else
                s.append('0');
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        return singleValue(s.toString(),mainChessColor);
    }
    private long getCowEvalue(int x,int y,int mainChessColor){
        StringBuilder s=new StringBuilder();
        int start=1;
        int cnt=0;
        while(start<=19){
            if(chessmap[start][y]!=EMPTY)
                break;
            ++start;
            ++cnt;
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        int end=19;
        cnt=0;
        while(end>=1){
            if(chessmap[end][y]!=EMPTY)
                break;
            --end;
            ++cnt;
        }
        for(int i=start;i<=end;i++){
            if(chessmap[i][y]==WHITE)
                s.append('1');
            else if(chessmap[i][y]==BLACK)
                s.append('2');
            else
                s.append('0');
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        return singleValue(s.toString(),mainChessColor);
    }
    private long getLeftSlantEvalue(int x,int y,int mainChessColor){
        StringBuilder s=new StringBuilder();
        int start_x,start_y;//左下到右上
        if(x+y-1<=19){
            start_x=x+y-1;
            start_y=1;
        }else{
            start_x=19;
            start_y=y+x-19;
        }
        int cnt=0;
        while (start_x>=1&&start_y<=19){
            if(chessmap[start_x][start_y]!=EMPTY)
                break;
            --start_x;
            ++start_y;
            ++cnt;
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        int end_x,end_y;
        if(x+y-1<=19){
            end_x=1;
            end_y=x+y-1;
        }else{
            end_x=x+y-19;
            end_y=19;
        }
        cnt=0;
        while(end_x<=19&&end_y>=1){
            if(chessmap[end_x][end_y]!=EMPTY)
                break;
            ++end_x;
            --end_y;
            ++cnt;
        }
        for(int i=start_x,j=start_y;i>=end_x&&j<=end_y;--i,++j){
            if(chessmap[i][j]==WHITE)
                s.append('1');
            else if(chessmap[i][j]==BLACK)
                s.append('2');
            else
                s.append('0');
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        return singleValue(s.toString(),mainChessColor);
    }
    private long getRightSlantEvalue(int x,int y,int mainChessColor){
        StringBuilder s=new StringBuilder();
        int start_x,start_y;//左上到右下
        int cnt=0;
        if(x<=y){
            start_x=1;
            start_y=y-x+1;
        }else {
            start_x=x-y+1;
            start_y=1;
        }
        while (start_x<=19&&start_y<=19){
            if(chessmap[start_x][start_y]!=EMPTY)
                break;
            ++start_x;
            ++start_y;
            ++cnt;
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        int end_x,end_y;
        if(x<=y){
            end_x=x+19-y;
            end_y=19;
        }else {
            end_x=19;
            end_y=19-x+y;
        }
        cnt=0;
        while(end_x>=1&&end_y>=1){
            if(chessmap[end_x][end_y]!=EMPTY)
                break;
            --end_x;
            --end_y;
            ++cnt;
        }
        for(int i=start_x,j=start_y;i<=end_x&&j<=end_y;i++,++j){
            if(chessmap[i][j]==WHITE)
                s.append('1');
            else if(chessmap[i][j]==BLACK)
                s.append('2');
            else
                s.append('0');
        }
        if(cnt==1){
            s.append('0');
        }else if(cnt==2){
            s.append("00");
        }else if(cnt==3){
            s.append("000");
        }else if(cnt>3){
            s.append("0000");
        }
        return singleValue(s.toString(),mainChessColor);
    }
    private long getPointEvalue(int x,int y,int mainChessColor){
        return getLineEvalue(x,y,mainChessColor)+
                getCowEvalue(x,y,mainChessColor)+
                getLeftSlantEvalue(x,y,mainChessColor)+
                getRightSlantEvalue(x,y,mainChessColor);
    }
    private long getPointEvalue(int x,int y){
        if(x>=y)
            return lineEvalueSum[x]+lineEvalueSum[y]+leftEvlueSum[x+y-1]+rightEvalueSum[20-(x-y+1)];
        return lineEvalueSum[x]+lineEvalueSum[y]+leftEvlueSum[x+y-1]+rightEvalueSum[38-(20-(y-x+1))];
    }
    private long singleValue(String str,int mainChessColor){
        int otherChessColor=(mainChessColor==WHITE)?BLACK:WHITE;
        return singleValue(str,0,mainChessColor)-(long)(1.1*singleValue(str,0,otherChessColor));
    }
    private long singleValue(String str, int valuestart, int mainChessColor){
        if(str.length()<6)
            return 0;
        int[] n= calcValue(str, valuestart,mainChessColor);
        long ans=0;
        if(n!=null)
             ans+=singleValue(str.substring(0,n[0]), valuestart +1,mainChessColor)+
                singleValue(str.substring(n[1]),0,mainChessColor)+value[n[2]];
        return ans;
    }
    private int[] calcValue(String str, int valuestart, int mainChessColor){//返回值 1:index 2：index+length 3：firstvalue
        int temp;
        if(mainChessColor==WHITE){
            for(int i = valuestart; i<whiteChessType.length; i++){
                for(String ss:whiteChessType[i]){
                    temp=str.indexOf(ss);
                    if(temp!=-1)
                        return new int[]{temp,temp+ss.length(),i};
                }
            }
        }else{
            for(int i = valuestart; i<blackChessType.length; i++){
                for(String ss:blackChessType[i]){
                    temp=str.indexOf(ss);
                    if(temp!=-1)
                        return new int[]{temp,temp+ss.length(),i};
                }
            }
        }
        return null;
    }
    private long evalue(int mainChessColor){
        long ans=0;
        for(int i=1;i<=19;i++){
            lineEvalueSum[i]=getLineEvalue(i,1,mainChessColor);
            ans+=lineEvalueSum[i];
            cowEvalueSum[i]=getCowEvalue(1,i,mainChessColor);
            ans+=cowEvalueSum[i];
        }
        for(int j=6;j<=19;j++){
            leftEvlueSum[j]=getLeftSlantEvalue(1,j,mainChessColor);
            ans+=leftEvlueSum[j];
        }
        for(int i=2;i<=14;i++){
            leftEvlueSum[i+19-1]=getLeftSlantEvalue(i,19,mainChessColor);
            ans+=leftEvlueSum[i+19-1];
        }
        for(int i=14;i>=1;i--){
            rightEvalueSum[20-i]=getRightSlantEvalue(i,1,mainChessColor);
            ans+=rightEvalueSum[20-i];
        }
        for(int j=2;j<=19;j++){
            rightEvalueSum[18+j]=getRightSlantEvalue(1,j,mainChessColor);
            ans+=rightEvalueSum[18+j];
        }
        return ans;
    }
    private long beta(int depth,long alpha,long beta,long valueSum,int mainChessColor,ArrayList<NodeFlag> flags){
        if (depth == 0) {
            return valueSum;
        }
        NodeFlag a,b;
        long value;
        int nextColor=(mainChessColor == WHITE) ? BLACK : WHITE;
        int size=flags.size();
        long temp_a,temp_aa;//old new
        long temp_b,temp_bb;
        long[][] Old=new long[2][4];
        long[][] New=new long[2][4];
        for(int i=0;i<size;i++){
            a=flags.get(i);
            if(!canDrop(a.x,a.y))
                continue;
            Old[0][0]=lineEvalueSum[a.x];
            Old[0][1]=cowEvalueSum[a.y];
            Old[0][2]=leftEvlueSum[a.x+a.y-1];
            if(a.x>=a.y)
                Old[0][3]=rightEvalueSum[20-(a.x-a.y+1)];
            else
                Old[0][3]=rightEvalueSum[38-(20-(a.y-a.x+1))];
            chessmap[a.x][a.y]=mainChessColor;
            if(isDropSuccess(a.x,a.y)){
                chessmap[a.x][a.y]=EMPTY;
                return -INF;
            }
            New[0][0]=getLineEvalue(a.x,a.y,AiColor);
            New[0][1]=getCowEvalue(a.x,a.y,AiColor);
            New[0][2]=getLeftSlantEvalue(a.x,a.y,AiColor);
            New[0][3]=getRightSlantEvalue(a.x,a.y,AiColor);
            temp_a=Old[0][0]+Old[0][1]+Old[0][2]+Old[0][3];
            valueSum-=temp_a;
            temp_aa=New[0][0]+New[0][1]+New[0][2]+New[0][3];
            valueSum+=temp_aa;
            for(int j=i+1;j<size;j++){
                b=flags.get(j);
                if(!canDrop(b.x,b.y))
                    continue;
                if(aiIsStop){
                    chessmap[a.x][a.y] = EMPTY;
                    return 0;
                }
                Old[1][0]=lineEvalueSum[b.x];
                Old[1][1]=cowEvalueSum[b.y];
                Old[1][2]=leftEvlueSum[b.x+b.y-1];
                if(b.x>=b.y)
                    Old[1][3]=rightEvalueSum[20-(b.x-b.y+1)];
                else
                    Old[1][3]=rightEvalueSum[38-(20-(b.y-b.x+1))];
                chessmap[b.x][b.y]=mainChessColor;
                New[1][0]=getLineEvalue(b.x,b.y,AiColor);
                New[1][1]=getCowEvalue(b.x,b.y,AiColor);
                New[1][2]=getLeftSlantEvalue(b.x,b.y,AiColor);
                New[1][3]=getRightSlantEvalue(b.x,b.y,AiColor);
                temp_b=Old[1][0]+Old[1][1]+Old[1][2]+Old[1][3];
                valueSum-=temp_b;
                temp_bb=New[1][0]+New[1][1]+New[1][2]+New[1][3];
                valueSum+=temp_bb;
                if(!isDropSuccess(b.x,b.y)){
                    value=alpha(depth-1,alpha,beta,valueSum,nextColor,flags)-chessMapCount[a.x][a.y]-chessMapCount[b.x][b.y];
                }else{
                    value=-INF;
                }
                chessmap[b.x][b.y] = EMPTY;
                valueSum-=temp_bb;
                valueSum+=temp_b;
                lineEvalueSum[b.x]=Old[1][0];
                cowEvalueSum[b.y]=Old[1][1];
                leftEvlueSum[b.x+b.y-1]=Old[1][2];
                if(b.x>=b.y)
                    rightEvalueSum[20-(b.x-b.y+1)]=Old[1][3];
                else
                    rightEvalueSum[38-(20-(b.y-b.x+1))]=Old[1][3];
                if(value<beta)
                    beta=value;
                if(alpha>=beta){
                    chessmap[a.x][a.y]=EMPTY;
                    lineEvalueSum[a.x]=Old[0][0];
                    cowEvalueSum[a.y]=Old[0][1];
                    leftEvlueSum[a.x+a.y-1]=Old[0][2];
                    if(a.x>=a.y)
                        rightEvalueSum[20-(a.x-a.y+1)]=Old[0][3];
                    else
                        rightEvalueSum[38-(20-(a.y-a.x+1))]=Old[0][3];
                    return beta;
                }
            }
            chessmap[a.x][a.y] = EMPTY;
            valueSum-=temp_aa;
            valueSum+=temp_a;
            lineEvalueSum[a.x]=Old[0][0];
            cowEvalueSum[a.y]=Old[0][1];
            leftEvlueSum[a.x+a.y-1]=Old[0][2];
            if(a.x>=a.y)
                rightEvalueSum[20-(a.x-a.y+1)]=Old[0][3];
            else
                rightEvalueSum[38-(20-(a.y-a.x+1))]=Old[0][3];
        }
        return beta;
    }
    private long alpha(int depth,long alpha,long beta,long valueSum,int mainChessColor,ArrayList<NodeFlag> flags) {
        ++cnt;
        return valueSum;//tuo depth;
    }
    public ArrayList<Node> maxMin(int depth,int mainChessColor){
        long alpha=-INF;
        long beta=INF;
        AiColor=mainChessColor;
        ArrayList<Node> bestNode=new ArrayList<>();
        ArrayList<NodeFlag> flags=allFlag();
        long valueSum=evalue(AiColor);
        long value = 0;
        int nextColor=mainChessColor==WHITE?BLACK:WHITE;
        int size=flags.size();
        NodeFlag a,b;
        long temp_a,temp_aa;//old new
        long temp_b,temp_bb;
        long[][] Old=new long[2][4];
        long[][] New=new long[2][4];
        for(int i=0;i<size;i++){
            a=flags.get(i);
            if(!canDrop(a.x,a.y))
                continue;
            Old[0][0]=lineEvalueSum[a.x];
            Old[0][1]=cowEvalueSum[a.y];
            Old[0][2]=leftEvlueSum[a.x+a.y-1];
            if(a.x>=a.y)
                Old[0][3]=rightEvalueSum[20-(a.x-a.y+1)];
            else
                Old[0][3]=rightEvalueSum[38-(20-(a.y-a.x+1))];
            chessmap[a.x][a.y]=mainChessColor;
            if (isDropSuccess(a.x, a.y)) {
                chessmap[a.x][a.y] = EMPTY;
                bestNode.clear();
                bestNode.add(new Node(a.x,a.y));
                return bestNode;
            }
            New[0][0]=getLineEvalue(a.x,a.y,AiColor);
            New[0][1]=getCowEvalue(a.x,a.y,AiColor);
            New[0][2]=getLeftSlantEvalue(a.x,a.y,AiColor);
            New[0][3]=getRightSlantEvalue(a.x,a.y,AiColor);
            temp_a=Old[0][0]+Old[0][1]+Old[0][2]+Old[0][3];
            valueSum-=temp_a;
            temp_aa=New[0][0]+New[0][1]+New[0][2]+New[0][3];
            valueSum+=temp_aa;
            for(int j=i+1;j<size;j++){
                b=flags.get(j);
                if (!canDrop(b.x,b.y))
                    continue;
                if(aiIsStop){
                    chessmap[a.x][a.y] = EMPTY;
                    return null;
                }
                Old[1][0]=lineEvalueSum[b.x];
                Old[1][1]=cowEvalueSum[b.y];
                Old[1][2]=leftEvlueSum[b.x+b.y-1];
                if(b.x>=b.y)
                    Old[1][3]=rightEvalueSum[20-(b.x-b.y+1)];
                else
                    Old[1][3]=rightEvalueSum[38-(20-(b.y-b.x+1))];
                chessmap[b.x][b.y]=mainChessColor;
                New[1][0]=getLineEvalue(b.x,b.y,AiColor);
                New[1][1]=getCowEvalue(b.x,b.y,AiColor);
                New[1][2]=getLeftSlantEvalue(b.x,b.y,AiColor);
                New[1][3]=getRightSlantEvalue(b.x,b.y,AiColor);
                temp_b=Old[1][0]+Old[1][1]+Old[1][2]+Old[1][3];
                valueSum-=temp_b;
                temp_bb=New[1][0]+New[1][1]+New[1][2]+New[1][3];
                valueSum+=temp_bb;
                if (!isDropSuccess(b.x, b.y)) {
                    value=beta(depth-1,alpha,beta,valueSum,nextColor,flags)+chessMapCount[a.x][a.y]+chessMapCount[b.x][b.y];
                }else{
                    chessmap[a.x][a.y] = EMPTY;
                    chessmap[b.x][b.y] = EMPTY;
                    bestNode.clear();
                    bestNode.add(new Node(a.x,a.y));
                    bestNode.add(new Node(b.x,b.y));
                    return bestNode;
                }
                chessmap[b.x][b.y] = EMPTY;
                valueSum-=temp_bb;
                valueSum+=temp_b;
                lineEvalueSum[b.x]=Old[1][0];
                cowEvalueSum[b.y]=Old[1][1];
                leftEvlueSum[b.x+b.y-1]=Old[1][2];
                if(b.x>=b.y)
                    rightEvalueSum[20-(b.x-b.y+1)]=Old[1][3];
                else
                    rightEvalueSum[38-(20-(b.y-b.x+1))]=Old[1][3];
                if(value==alpha){
                    bestNode.add(new Node(a.x,a.y));
                    bestNode.add(new Node(b.x,b.y));
                }
                if(value>alpha){
                    alpha=value;
                    bestNode.clear();
                    bestNode.add(new Node(a.x,a.y));
                    bestNode.add(new Node(b.x,b.y));
                }
            }
            chessmap[a.x][a.y] = EMPTY;
            valueSum-=temp_aa;
            valueSum+=temp_a;
            lineEvalueSum[a.x]=Old[0][0];
            cowEvalueSum[a.y]=Old[0][1];
            leftEvlueSum[a.x+a.y-1]=Old[0][2];
            if(a.x>=a.y)
                rightEvalueSum[20-(a.x-a.y+1)]=Old[0][3];
            else
                rightEvalueSum[38-(20-(a.y-a.x+1))]=Old[0][3];
        }
        int rand= new Random().nextInt(bestNode.size()/2);
        ArrayList<Node> temp=new ArrayList<>();
        temp.add(bestNode.get(rand*2));
        temp.add(bestNode.get(rand*2+1));
        return temp;
    }
}
