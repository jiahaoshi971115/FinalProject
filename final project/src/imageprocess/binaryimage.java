package imageprocess;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class binaryimage {
    private int gray[][]=null;//存储图像灰度值
    public int brimage[][]=null;//存储图像二值化后灰度值
    private	int gra[][]=null;//给图像添白框，方便去噪
    public BufferedImage image;
    public void brmatrix(BufferedImage bi) throws IOException {

        int h=bi.getHeight();//获取图像的高
        int w=bi.getWidth();//获取图像的宽
        gray=new int[w][h];
        brimage=new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                gray[x][y]=getGray(bi.getRGB(x, y));
            }
        }
        Brighter(gray,w,h);
        gra=new int[w+4][h+4];
        for(int i=0;i<w+4;i++){
            for(int j=0; j<h+4; j++){
                if(i>1&&i<w-1&&j>1&&j<h-1){
                    gra[i][j]=gray[i-2][j-2];
                }
                else{
                    gra[i][j]=0;}
            }
        }
        BufferedImage nbi=new BufferedImage(w,h,BufferedImage.TYPE_BYTE_BINARY);
        int SW=125;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y<h; y++) {
                if(getAverageColor(gra, x, y, w, h)>SW){
                    int max=new Color(255,255,255).getRGB();
                    nbi.setRGB(x, y, max);
                    brimage[x][y]=1;
                }else{
                    int min=new Color(0,0,0).getRGB();
                    nbi.setRGB(x, y, min);
                    brimage[x][y]=0;
                }
            }
        }
        this.image=nbi;
        System.gc();
    }
    private int getGray(int rgb){
        String str=Integer.toHexString(rgb);
        int r=Integer.parseInt(str.substring(2,4),16);
        int g=Integer.parseInt(str.substring(4,6),16);
        int b=Integer.parseInt(str.substring(6,8),16);
        Color c=new Color(rgb);
        r=c.getRed();
        g=c.getGreen();
        b=c.getBlue();
        int top=(r+g+b)/3;
        return (int)(top);
    }

    private int  getAverageColor(int[][] gray, int x, int y, int w, int h)
    {
        int rs=0;
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                rs=gray[x+i][y+j]+rs;}
        }
        return rs / 25;
    }
    public static void Brighter(int[][]gray,int w,int h){
        for(int x=0;x<w;x++){
            for(int y=0;y<h;y++){
                gray[x][y]=(int) Math.floor(gray[x][y]*1.25);
                if(gray[x][y]>255){
                    gray[x][y]=255;
                }
            }
        }
    }
}
