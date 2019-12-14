package main;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.io.BufferedReader;
import imageprocess.getimage;

public class GUI extends JFrame {

    private JFrame jFrame;
    private BufferedImage img;//用于显示输入图片
    private JButton sure;//手写输入确定
    private JButton cancel;//手写输入确定
    private JButton recognition;//识别
    private JButton open;
    private JTextField result;
    private int[][] getmatrix=new int[28][28];
    private int[][] getmatrixprint=new int[28][28];
    private JTextPane imgtextarea;
    private JLabel imglabel;
    private JFileChooser choose;//选择文件
    //private int k=0;

    public GUI() throws IOException {

        jFrame=new JFrame("Digital Recognition");
        jFrame.setBounds(0, 0, 765, 800);
        jFrame.setLayout(null);
        recognition=new JButton("Identify Results");

        open=new JButton("Open Picture");
        sure=new JButton("confirm");
        cancel=new JButton("redraw");
        JPanel resultpanel = new JPanel();
        final mypanel panel = new mypanel();//新建画板
        Container contentPane = getContentPane();
        contentPane.setBounds(0, 0,350,350);
        contentPane.add(panel);
        jFrame.add(contentPane);
        JPanel draw=new JPanel();//画板桌布
        draw.setBounds(0, 0, 380,420);
        draw.setLayout(null);
        draw.setBackground(Color.lightGray);
        jFrame.add(draw);

        draw.add(sure);
        sure.setBounds(30, 370, 90, 30);
        draw.add(cancel);
        cancel.setBounds(210, 370, 90, 30);
        open.setBounds(420, 320, 120, 30);

        recognition.setBounds(560, 320, 150, 30);
        imgtextarea=new JTextPane();
        Style style=new StyleContext().new NamedStyle();
        StyleConstants.setLineSpacing(style,-0.1f);
        StyleConstants.setFontSize(style, 7);
        StyleConstants.setBold(style, true);
        imgtextarea.setLogicalStyle(style);
        imglabel=new JLabel();
        imgtextarea.setSize(50, 70);
        imgtextarea.setBounds(300, 0, 200, 60);
        imglabel.setBounds(200,10,100,100);
        imgtextarea.setEditable(false);

        choose = new JFileChooser();
        choose.setCurrentDirectory(new File(""));

        resultpanel.add(imglabel);
        resultpanel.add(imgtextarea);
        result=new JTextField();
        result.setBounds(560, 360, 140, 50);
        result.setVisible(true);
        resultpanel.setBounds(381, 0, 350, 310);
        resultpanel.setBackground(Color.gray);
        jFrame.add(resultpanel);
        jFrame.add(result);
        jFrame.add(recognition);
        jFrame.add(open);

        jFrame.setSize(761, 450);
        jFrame.setVisible(true);
        sure.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionevent) {
                // TODO Auto-generated method stub
                BufferedImage image=new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics gs=image.getGraphics();
                panel.paintAll(gs);
                gs.drawImage(image, 0, 0, panel.getWidth(), panel.getHeight(), null);
                try {
                    ImageIO.write(image, "png", new File("src/main/save.jpg"));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    getmatrix=getimage.getMatirx(image);

                    if(image.getHeight()>28||image.getWidth()>28){
                        image=getimage.scale(image, 28, 28);}
                    imglabel.setIcon(new ImageIcon((Image)image));//把图片作为icon显示

                    for (int i = 0; i < getmatrix.length; ++i) {
                        for (int j = 0; j < getmatrix[0].length; ++j) {
                            getmatrixprint[i][j] = getmatrix[i][j];
                        }
                    }
                    for (int i = 0; i < getmatrix.length-1; i++) {
                        for (int j = i+1; j < getmatrix.length; j++) {
                            int temp=getmatrix[i][j];
                            getmatrix[i][j]=getmatrix[j][i];
                            getmatrix[j][i]=temp;
                        }

                    }

                    File file = new File("src/main/finalproject.txt");  //存放数组数据的文件

                    FileWriter out = new FileWriter(file);  //文件写入流

                    //将数组中的数据写入到文件中。每行各数据之间TAB间隔
                    for(int i=0;i<getmatrixprint.length;i++){
                        for(int j=0;j<getmatrixprint[0].length;j++){
                            out.write(getmatrixprint[i][j]+"\t");
                        }
                        out.write("\r\n");
                    }
                    out.close();

                    imgtextarea.setText("");
                    String s="";
                    for(int i=0;i<getmatrix[0].length;i++) {
                        for(int j=0;j<getmatrix.length;j++) {
                            if(j==getmatrix[0].length-1) {
                                s=s+getmatrix[j][i]+"\n";
                                imgtextarea.setText(s);
                            }
                            else {
                                s=s+getmatrix[j][i]+",";
                                imgtextarea.setText(s);
                            }
                        }
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionevent) {
                // TODO Auto-generated method stub
                panel.cleanAll();
            }
        });

        recognition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionevent) {

                try {
                    Process pro = Runtime.getRuntime().exec("python C:\\Users\\15804\\loadmodel.py");// 执行py文件
                    pro.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                // TODO Auto-generated method stub
                String res = "";
                String s;
                try{
                    BufferedReader in =new BufferedReader(new FileReader("C:\\Users\\15804\\result.txt"));
                    int i=0;
                    while((s=in.readLine())!=null){
                        res += s.substring(0,1);
                        i++;
                    }
                }
                catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                catch(IOException e){
                    e.printStackTrace();
                }

                result.setText(res);
            }
        });

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionevent) {
                // TODO Auto-generated method stub
                int result = choose.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    String name = choose.getSelectedFile().getPath();
                    try {
                        img=ImageIO.read(new File(name));
                        if(img.getWidth()>28||img.getHeight()>28)
                        {img=getimage.scale(img, 28, 28);//缩放图片
                        }
                        imglabel.setIcon(new ImageIcon((Image)img));//把图片作为icon显示


                        String s=" ";
                        for(int i=0;i<getmatrix[0].length;i++) {
                            for(int j=0;j<getmatrix.length;j++) {
                                if(j==getmatrix[0].length-1) {
                                    s=s+getmatrix[j][i]+"\n";
                                    imgtextarea.setText(s);
                                }
                                else {
                                    s=s+getmatrix[j][i]+",";
                                    imgtextarea.setText(s);
                                }
                            }
                        }

                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });

    }
    public static void main(String[] args) throws IOException {


        GUI gui=new GUI();

    }
}
