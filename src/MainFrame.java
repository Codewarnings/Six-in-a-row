import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public final class MainFrame extends JFrame {
    private final int Size=1000;
    public final int Max=19;
    private static JPanel jpLeft;
    private static JPanel jpRight;
    private static final String strTime="00:00:00 00";
    private final LinkedList<Long> firstTime=new LinkedList<>();
    private final LinkedList<Long> lastTime=new LinkedList<>();
    private int personColor;
    private Label firstLabel;
    private Label lastLabel;
    private JButton firstHand;
    private JButton lastHand;
    private JButton repentance;
    private JButton continu;
    private JButton restar;
    private JButton save;
    private JScrollPane jScrollPane;
    private JTextArea jTextArea;
    private ChessMap chessMap=new ChessMap();
    private TimerThread thred;
    private final AddActionListener addActionListener=new AddActionListener();
    MainFrame(){
        setTitle("六子棋");
        setBounds(300,10,1320,1020);
        JSplitPane jsp=new JSplitPane();
        jpLeftInit();
        jsp.setLeftComponent(jpLeft);
        jsp.setDividerSize(1);
        JpRightInit();
        jsp.setRightComponent(jpRight);
        jsp.setContinuousLayout(true);
        jsp.setDividerLocation(1000);
        jsp.setEnabled(false);// 禁止拖动分割条
        setContentPane(jsp);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
    private void jpLeftInit(){
        jpLeft=new JPanel(){//绘制棋盘,匿名类
            @Override
            public void paint(Graphics g){
                g.clearRect(0,0,Size,Size+20);
                g.setColor(Color.ORANGE);
                g.fillRect(0,0,Size,Size+20);
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2.0f));
                for(int i=1;i<=Max;i++){
                    g2.drawLine(50,i*50,950,i*50);
                    g2.drawLine(i*50,50,i*50,950);
                }
                Font font=new Font("黑体",Font.BOLD,20);
                g2.setFont(font);
                for(int i=1;i<=Max;i++){
                    g2.drawString(Character.toString((char)('A'+i-1)),i*50-5,975);
                    g2.drawString(Integer.toString(19-i+1),10,i*50+6);
                }
                g2.fillOval(194,194,12,12);
                g2.fillOval(794,194,12,12);
                g2.fillOval(494,494,12,12);
                g2.fillOval(194,794,12,12);
                g2.fillOval(794,794,12,12);
                int size= chessMap.nodes.size();
                if(size==0)
                    return;
                Node node= chessMap.nodes.get(0);
                g.fillOval(node.y*50-20,node.x*50-20,40,40);
                g.setColor(Color.RED);
                g.drawString(Integer.toString(1),node.y*50-5,node.x*50+5);
                for(int i = 1; i< size; i++){
                    node= chessMap.nodes.get(i);
                    if((i-1)%4==0||(i-2)%4==0){
                        g.setColor(Color.WHITE);
                    }else{
                        g.setColor(Color.BLACK);
                    }
                    g.fillOval(node.y*50-20,node.x*50-20,40,40);
                    g.setColor(Color.RED);
                    if(i+1<10){
                        g.drawString(Integer.toString(i+1),node.y*50-5,node.x*50+5);
                    }else if(i+1<100){
                        g.drawString(Integer.toString(i+1),node.y*50-8,node.x*50+5);
                    }else{
                        g.drawString(Integer.toString(i+1),node.y*50-11,node.x*50+5);
                    }
                }
                if(size<=3){
                    node= chessMap.nodes.get(0);
                    g.drawRect(node.y*50-21,node.x*50-21,42,42);
                    return;
                }
                if(chessMap.lastChessColor()!= chessMap.nextChessColor()){
                    node= chessMap.nodes.get(size-1);
                    g.drawRect(node.y*50-21,node.x*50-21,42,42);
                    node= chessMap.nodes.get(size-2);
                }else{
                    int temp=(size-2)/2*2;
                    node= chessMap.nodes.get(temp-1);
                    g.drawRect(node.y*50-21,node.x*50-21,42,42);
                    node= chessMap.nodes.get(temp);
                }
                g.drawRect(node.y*50-21,node.x*50-21,42,42);
            }
        };
        jpLeft.addMouseListener(addActionListener);
    }
    private void JpRightInit(){
        jpRight=new JPanel();
        jpRight.setLayout(null);//空布局
        jpRight.setBorder(BorderFactory.createLoweredBevelBorder());
        Font font=new Font("黑体",Font.BOLD,18);
        firstLabel =new Label();
        firstLabel.setFont(font);
        firstLabel.setText(strTime);
        firstLabel.setBounds(190,50,150,50);
        jpRight.add(firstLabel);
        lastLabel =new Label();
        lastLabel.setFont(font);
        lastLabel.setText(strTime);
        lastLabel.setBounds(190,130,150,50);
        jpRight.add(lastLabel);
        ButtonActionListener buttonActionListener=new ButtonActionListener();
        firstHand=new JButton("先手");
        firstHand.setBounds(20,50,150,50);
        firstHand.setFont(font);
        firstHand.addActionListener(buttonActionListener);
        jpRight.add(firstHand);
        lastHand =new JButton("后手");
        lastHand.setBounds(20,130,150,50);
        lastHand.setFont(font);
        lastHand.addActionListener(buttonActionListener);
        jpRight.add(lastHand);
        repentance=new JButton("悔棋");
        repentance.setFont(font);
        repentance.setBounds(20,210,120,50);
        repentance.addActionListener(buttonActionListener);
        repentance.setEnabled(false);
        jpRight.add(repentance);
        continu=new JButton("继续");
        continu.setFont(font);
        continu.setBounds(180,210,120,50);
        continu.addActionListener(buttonActionListener);
        continu.setEnabled(false);
        jpRight.add(continu);
        restar=new JButton("重新开始");
        restar.setFont(font);
        restar.setBounds(70,290,160,50);
        restar.setEnabled(false);
        restar.addActionListener(buttonActionListener);
        jpRight.add(restar);
        save=new JButton("保存棋谱");
        save.setFont(font);
        save.setBounds(70,370,160,50);
        save.addActionListener(buttonActionListener);
        save.setEnabled(false);
        jpRight.add(save);
        jTextArea=new JTextArea("落子情况:\n");
        jTextArea.setFont(new Font("宋体",Font.PLAIN,20));
        jTextArea.setEnabled(false);//不可编辑
        jTextArea.setDisabledTextColor(Color.BLACK);//字体颜色
        jScrollPane=new JScrollPane();
        jScrollPane.setViewportView(jTextArea);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setBounds(60,450,200,500);
        jpRight.add(jScrollPane);
        //jTextArea.paintImmediately(jTextArea.getBounds());
    }
    private void printChess(){
        Node node=chessMap.nodes.get(chessMap.nodes.size()-1);
        if(chessMap.thisChessColor(chessMap.nodes.size())==ChessMap.WHITE){
            jTextArea.append("White:("+(char)('A'+node.y-1)+","+(19-node.x+1)+")\n");
        }else{
            jTextArea.append("Black:("+(char)('A'+node.y-1)+","+(19-node.x+1)+")\n");
        }
        Point p=new Point();
        p.setLocation(0,jTextArea.getLineCount()*20);
        jScrollPane.getViewport().setViewPosition(p);
    }
    private void saveChess() throws  IOException {
        String[] name;
        while(true){
            String Str=JOptionPane.showInputDialog(this,"请输入先后手参赛队伍名,英文分号间隔","输入对话框",JOptionPane.PLAIN_MESSAGE);
            name=Str.split(";");
            if(name.length==2)
                break;
            JOptionPane.showMessageDialog(null,"输入错误,请重新输入");
        }
        Date date=new Date();
        SimpleDateFormat ft=new SimpleDateFormat("yyyy.MM.dd");
        File f = new File("C6-先手参赛队 "+name[0]+" vs 后手参赛队 "+name[1]+(chessMap.lastChessColor()==ChessMap.BLACK?"-先手胜":"-后手胜")+"-"+ft.format(date)+"安徽蚌埠-ACG.txt");
        if (!f.exists())
            f.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        ft=new SimpleDateFormat("yyyy.MM.dd HH:mm");
        bw.write("{[C6][先手参赛队 "+name[0]+"][后手参赛队 "+name[1]+"]["+(chessMap.lastChessColor()==ChessMap.BLACK?"先手胜":"后手胜")+"]["+ft.format(date)+" 安徽蚌埠][2021 ACG];");
        for (int i=0;i<chessMap.nodes.size();i++){
            Node node=chessMap.nodes.get(i);
             if(chessMap.thisChessColor(i+1)==ChessMap.BLACK)
                 bw.write(name[0]+"("+(char)('A'+node.y-1)+","+(19-node.x+1)+");");
             else
                 bw.write(name[1]+"("+(char)('A'+node.y-1)+","+(19-node.x+1)+");");
        }
        bw.close();
        JOptionPane.showMessageDialog(null,"保存成功,文件位于:"+f.getAbsolutePath());
    }
    private String formatch(long time){
        int hour,minute,second,milli;
        time/=10;
        milli = (int) (time % 100);
        time = time / 100;
        second = (int) (time % 60);
        time = time / 60;
        minute = (int) (time % 60);
        time = time / 60;
        hour = (int) (time % 60);
        return String.format("%02d:%02d:%02d %02d", hour, minute, second, milli);
    }
    class ButtonActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==firstHand){
                personColor =ChessMap.BLACK;
                restar.setEnabled(true);
                firstHand.setEnabled(false);
                lastHand.setEnabled(false);
                save.setEnabled(true);
                firstTime.add(0L);
                lastTime.add(0L);
                thred=new TimerThread(firstLabel,firstTime);
                thred.isStop=false;
                thred.start();
            }else if (e.getSource()==lastHand){
                personColor =ChessMap.WHITE;
                restar.setEnabled(true);
                firstHand.setEnabled(false);
                lastHand.setEnabled(false);
                repentance.setEnabled(true);
                save.setEnabled(true);
                firstTime.add(0L);
                lastTime.add(0L);
                firstTime.add(10L);
                chessMap.chessmap[10][10]=ChessMap.BLACK;
                chessMap.updataFlag(10,10,1);
                chessMap.nodes.add(new Node(10,10));
                printChess();
                jpLeft.repaint();
                jpLeft.paintImmediately(jpLeft.getBounds());
                firstLabel.setText(formatch(firstTime.getLast()));
                thred=new TimerThread(lastLabel, lastTime);
                thred.isStop=false;
                thred.start();
            }else if(e.getSource()==repentance){
                if(chessMap.nodes.size()==1&&continu.isEnabled())
                    repentance.setEnabled(false);
                thred.isStop=true;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                int otherChessColor =personColor==ChessMap.WHITE?ChessMap.BLACK:ChessMap.WHITE;
                if(chessMap.nextChessColor()== otherChessColor&& !continu.isEnabled()){
                    chessMap.aiIsStop=true;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    chessMap.aiIsStop=false;
                    if(otherChessColor ==ChessMap.BLACK){
                        firstTime.removeLast();
                        firstLabel.setText(formatch(firstTime.getLast()));
                    }
                    else{
                        lastTime.removeLast();
                        lastLabel.setText(formatch(lastTime.getLast()));
                    }
                    if(chessMap.nextChessColor()==ChessMap.WHITE)
                        jTextArea.append("White: Stop\n");
                    else
                        jTextArea.append("Black: Stop\n");
                    repentance.setEnabled(true);
                }else if(!continu.isEnabled()){
                    if(personColor ==ChessMap.BLACK){
                        firstTime.removeLast();
                        firstLabel.setText(formatch(firstTime.getLast()));
                    }
                    else{
                        lastTime.removeLast();
                        lastLabel.setText(formatch(lastTime.getLast()));
                    }
                    if(personColor==ChessMap.WHITE)
                        jTextArea.append("White: Stop\n");
                    else
                        jTextArea.append("Black: Stop\n");
                }else if(chessMap.nodes.size()==1){
                    firstTime.removeLast();
                    //if(!continu.isEnabled())
                    //    lastTime.removeLast();
                    firstLabel.setText(formatch(firstTime.getLast()));
                    lastLabel.setText(formatch(lastTime.getLast()));
                    Node node=chessMap.nodes.get(0);
                    chessMap.updataFlag(node.x,node.y,-1);
                    jTextArea.append("Black悔棋:("+node.x+","+node.y+")\n");
                    chessMap.chessmap[node.x][node.y]=ChessMap.EMPTY;
                    chessMap.nodes.remove(0);
                }else if(chessMap.lastChessColor()==chessMap.nextChessColor()){
                    Node node=chessMap.nodes.get(chessMap.nodes.size()-1);
                    if(chessMap.lastChessColor()==ChessMap.WHITE){
                        lastTime.removeLast();
                        lastLabel.setText(formatch(lastTime.getLast()));
                        jTextArea.append("White悔棋:("+node.x+","+node.y+")\n");
                    }else{
                        firstTime.removeLast();
                        firstLabel.setText(formatch(firstTime.getLast()));
                        jTextArea.append("Black悔棋:("+node.x+","+node.y+")\n");
                    }
                    chessMap.chessmap[node.x][node.y]=ChessMap.EMPTY;
                    chessMap.updataFlag(node.x,node.y,-1);
                    chessMap.nodes.remove(chessMap.nodes.size()-1);
                }else{
                    Node a=chessMap.nodes.get(chessMap.nodes.size()-1);
                    Node b=chessMap.nodes.get(chessMap.nodes.size()-2);
                    if(chessMap.lastChessColor()==ChessMap.WHITE){
                        if(!continu.isEnabled()&&!chessMap.isDropSuccess(a.x,a.y))
                            firstTime.removeLast();
                        lastTime.removeLast();
                        lastLabel.setText(formatch(lastTime.getLast()));
                        jTextArea.append("White悔棋:("+a.x+","+a.y+")\n");
                        jTextArea.append("White悔棋:("+b.x+","+b.y+")\n");
                    }else{
                        firstTime.removeLast();
                        if(!continu.isEnabled()&&!chessMap.isDropSuccess(a.x,a.y))
                            lastTime.removeLast();
                        firstLabel.setText(formatch(firstTime.getLast()));
                        jTextArea.append("Black悔棋:("+a.x+","+a.y+")\n");
                        jTextArea.append("Black悔棋:("+b.x+","+b.y+")\n");
                    }
                    chessMap.chessmap[a.x][a.y]=ChessMap.EMPTY;
                    chessMap.chessmap[b.x][b.y]=ChessMap.EMPTY;
                    chessMap.updataFlag(a.x,a.y,-1);
                    chessMap.updataFlag(b.x,b.y,-1);
                    chessMap.nodes.remove(chessMap.nodes.size()-1);
                    chessMap.nodes.remove(chessMap.nodes.size()-1);
                }
                continu.setEnabled(true);
                Point p=new Point();
                p.setLocation(0,jTextArea.getLineCount()*20);
                jScrollPane.getViewport().setViewPosition(p);
                jpLeft.repaint();
                jpLeft.paintImmediately(jpLeft.getBounds());
            }else if(e.getSource()==continu){
                continu.setEnabled(false);
                if(chessMap.nodes.size()==0&&personColor==ChessMap.WHITE){
                    firstTime.add(10L);
                    firstLabel.setText(formatch(firstTime.getLast()));
                    chessMap.chessmap[10][10]=ChessMap.BLACK;
                    chessMap.updataFlag(10,10,1);
                    chessMap.nodes.add(new Node(10,10));
                    jpLeft.repaint();
                    jpLeft.paintImmediately(jpLeft.getBounds());
                    thred=new TimerThread(lastLabel, lastTime);
                    thred.isStop=false;
                    printChess();
                    thred.start();
                    return;
                }
                if(chessMap.nextChessColor()==ChessMap.WHITE){
                    thred=new TimerThread(lastLabel,lastTime);
                }else{
                    thred=new TimerThread(firstLabel,firstTime);
                }
                thred.isStop=false;
                thred.start();
            }else if(e.getSource()==restar){
                if(JOptionPane.showConfirmDialog(null,"是否重新开始","确认对话框",JOptionPane.YES_NO_OPTION)==0){
                    firstHand.setEnabled(true);
                    lastHand.setEnabled(true);
                    repentance.setEnabled(false);
                    continu.setEnabled(false);
                    restar.setEnabled(false);
                    chessMap=new ChessMap();
                    jpLeft.repaint();
                    jpLeft.paintImmediately(jpLeft.getBounds());
                    jTextArea.setText("落子情况:\n");
                    save.setEnabled(false);
                    thred.isStop=true;
                    firstTime.clear();
                    lastTime.clear();
                    firstTime.add(0L);
                    lastTime.add(0L);
                    firstLabel.setText(strTime);
                    lastLabel.setText(strTime);
                }
            }else{
                try{
                    saveChess();
                }catch (IOException ee){
                    ee.printStackTrace();
                    JOptionPane.showMessageDialog(null,"保存失败");
                }
            }
        }
    }
    class AddActionListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (firstLabel.isEnabled()&&lastHand.isEnabled()){
                JOptionPane.showMessageDialog(null,"请先选择先手/后手");
                return;
            }
            if(continu.isEnabled()){
                JOptionPane.showMessageDialog(null,"请先点击继续按钮");
                return;
            }
            if(chessMap.nodes.size()>0){
                Node node=chessMap.nodes.get(chessMap.nodes.size()-1);
                if(chessMap.isDropSuccess(node.x,node.y)){
                    if(chessMap.lastChessColor()==ChessMap.WHITE){
                        JOptionPane.showMessageDialog(null,"白棋胜利，请选择重新开始/悔棋/保存棋谱");
                    }else{
                        JOptionPane.showMessageDialog(null,"白棋胜利，请选择重新开始/悔棋/保存棋谱");
                    }
                    return;
                }
            }
            int x = e.getY();
            int y = e.getX();
            if (x < 20 || x > 970 || y < 20 || y > 970)
                return;
            if (x % 50 <= 20) {
                x = x / 50;
            } else if (x % 50 >= 30) {
                x = (x + 20) / 50;
            } else {
                return;
            }
            if (y % 50 <= 20) {
                y = y / 50;
            } else if (y % 50 >= 30) {
                y = (y + 20) / 50;
            } else {
                return;
            }
            if(!chessMap.canDrop(x,y))
                return;
            repentance.setEnabled(true);
            chessMap.chessmap[x][y]= chessMap.nextChessColor();
            chessMap.updataFlag(x,y,1);
            chessMap.nodes.add(new Node(x,y));
            repentance.setEnabled(true);
            printChess();
            if(chessMap.nodes.size()==1){
                thred.isStop=true;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            jpLeft.repaint();
            jpLeft.paintImmediately(jpLeft.getBounds());
            if(chessMap.isDropSuccess(x,y)){
                thred.isStop=true;
                if(chessMap.lastChessColor()==ChessMap.WHITE){
                    jTextArea.append("White:win!\n");
                    JOptionPane.showMessageDialog(null,"白棋胜利，请选择重新开始/悔棋/保存棋谱");
                }else{
                    jTextArea.append("Black:win!\n");
                    JOptionPane.showMessageDialog(null,"黑棋胜利，请选择重新开始/悔棋/保存棋谱");
                }
                Point p=new Point();
                p.setLocation(0,jTextArea.getLineCount()*20);
                jScrollPane.getViewport().setViewPosition(p);
                return;
            }
            if(chessMap.nodes.size()==19*19){
                thred.isStop=true;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                JOptionPane.showMessageDialog(null,"平局，请选择是否重新开始/悔棋/保存棋谱");
                return;
            }
            if(chessMap.nextChessColor()!=personColor){
                jpLeft.removeMouseListener(addActionListener);
                thred.isStop=true;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                AICalcThread aiCalcThread=new AICalcThread();
                aiCalcThread.start();
            }
        }
    }
    private class AICalcThread extends Thread{
        @Override
        public void run(){
            int mainChessColor=personColor==ChessMap.WHITE?ChessMap.BLACK:ChessMap.WHITE;
            if(mainChessColor==ChessMap.WHITE){
                thred=new TimerThread(lastLabel,lastTime);
            }else{
                thred=new TimerThread(firstLabel,firstTime);
            }
            thred.isStop=false;
            thred.start();
            ArrayList<Node> nodes=chessMap.maxMin(2,mainChessColor);
            if(chessMap.aiIsStop){
                thred.isStop=true;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                jpLeft.addMouseListener(addActionListener);
                return;
            }
            jpLeft.addMouseListener(addActionListener);
            Node a=nodes.get(0);
            Node b=nodes.get(1);
            chessMap.chessmap[a.x][a.y]=mainChessColor;
            chessMap.nodes.add(a);
            chessMap.updataFlag(a.x,a.y,1);
            jpLeft.repaint();
            jpLeft.paintImmediately(jpLeft.getBounds());
            thred.isStop=true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            printChess();
            //System.err.println(ChessMap.cnt);
            if(chessMap.isDropSuccess(a.x,a.y)){
                thred.isStop=true;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                if(chessMap.lastChessColor()==ChessMap.WHITE){
                    jTextArea.append("White:win!\n");
                    JOptionPane.showMessageDialog(null,"白棋胜利，请选择重新开始/悔棋/保存棋谱");
                }else{
                    jTextArea.append("Black:win!\n");
                    JOptionPane.showMessageDialog(null,"黑棋胜利，请选择重新开始/悔棋/保存棋谱");
                }
                Point p=new Point();
                p.setLocation(0,jTextArea.getLineCount()*20);
                jScrollPane.getViewport().setViewPosition(p);
                return;
            }
            chessMap.chessmap[b.x][b.y]=mainChessColor;
            chessMap.nodes.add(b);
            chessMap.updataFlag(b.x,b.y,1);
            jpLeft.repaint();
            jpLeft.paintImmediately(jpLeft.getBounds());
            printChess();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jpLeft.repaint();
            jpLeft.paintImmediately(jpLeft.getBounds());
            if(chessMap.isDropSuccess(b.x,b.y)){
                thred.isStop=true;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                if(chessMap.lastChessColor()==ChessMap.WHITE){
                    jTextArea.append("White:win!\n");
                    JOptionPane.showMessageDialog(null,"白棋胜利，请选择重新开始/悔棋/保存棋谱");
                }else{
                    jTextArea.append("Black:win!\n");
                    JOptionPane.showMessageDialog(null,"黑棋胜利，请选择重新开始/悔棋/保存棋谱");
                }
                Point p=new Point();
                p.setLocation(0,jTextArea.getLineCount()*20);
                jScrollPane.getViewport().setViewPosition(p);
                return;
            }
            if(personColor==ChessMap.WHITE)
                thred=new TimerThread(lastLabel,lastTime);
            else
                thred=new TimerThread(firstLabel,firstTime);
            thred.isStop=false;
            thred.start();
        }
    }
    private class TimerThread extends Thread{
        public boolean isStop=true;
        private final Label label;
        private final LinkedList<Long> time;
        TimerThread(Label label,LinkedList<Long> time){
            this.label=label;
            this.time=time;
        }
        @Override
        public void run(){
            long updateTime=0;
            long lasttime=time.getLast();
            long pauseTime = System.currentTimeMillis();
            while (!isStop) {
                updateTime = System.currentTimeMillis() - pauseTime +lasttime;
                label.setText(formatch(updateTime));
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            time.add(updateTime);
        }
    }
}
