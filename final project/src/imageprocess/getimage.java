package imageprocess;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class getimage {
    public final static int[][] getMatirx(BufferedImage bi) throws IOException{
        binaryimage biimg=new binaryimage();//创建二值图类，分别保存二值化后的图片及矩阵
        int h=bi.getHeight();
        int w=bi.getWidth();
        if(h>800||w>800){
            bi=scale(bi, 800, 800);
            h=bi.getHeight();
            w=bi.getWidth();
        }
        biimg.brmatrix(bi);//二值图保；

        int bi_matrix[][]=biimg.brimage;//二值矩阵
        int left=0,right=0,top=0,below=0;
        int row[]=new int[w];
        for(int i=0;i<w;i++){
            int s=0;
            for(int j=0;j<h;j++){
                s=s+bi_matrix[i][j];
            }
            row[i]=s;
        }
        int line[]=new int[h];
        for(int j=0;j<h;j++){
            int s=0;
            for(int i=0;i<w;i++){
                s=s+bi_matrix[i][j];
            }
            line[j]=s;
        }
        for(int i=1;i<w;i++){
            if(row[i]>=2){
                if(left==0){
                    left=i;
                }
                if(right<i){
                    right=i;}
            }
        }
        for(int i=1;i<h;i++){
            if(line[i]>=2){
                if(top==0){
                    top=i;
                }
                if(below<i){
                    below=i;}
            }
        }
        int new_h;int new_w;
        new_h=(28-(below-top)%28)-top+below;
        new_w=(28-(right-left)%28)-left+right;

        if((new_h/new_w)>=2){
            new_w=(new_h/2%28)+new_h/2;
            top=top-(28-(below-top)%28)/2;
            left=left-(new_w-right+left)/2;
        }
        else{
            top=top-(28-(below-top)%28)/2;
            left=left-(28-(right-left)%28)/2;
        }
        biimg.image=cut(biimg.image, left, top, w, h, new_w, new_h);
        int InputMatrix[][]=new int[28][28];
        InputMatrix=cut2(biimg.image, 28, 28);

        return InputMatrix;
    }
    public final static BufferedImage scale(BufferedImage bi, int height, int width) {
        double ratio = 0.0; //  缩放比例
        Image temp = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
            if (bi.getHeight() > bi.getWidth()) {
                ratio = (new Integer(height)).doubleValue()/ bi.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue()/ bi.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(AffineTransform
                    .getScaleInstance(ratio, ratio), null);
            temp = op.filter(bi, null);
        }
        return toBufferedImage(temp);
    }
    public final static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();
        boolean hasAlpha = false;
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
        }
        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }
    public final static BufferedImage cut(BufferedImage bi,int x, int y,int w,int h,int new_w,int new_h) throws IOException {
        Image image = bi.getScaledInstance(w, h,
                Image.SCALE_DEFAULT);
        ImageFilter cropFilter = new CropImageFilter(x, y, new_w, new_h);
        Image img = Toolkit.getDefaultToolkit().createImage(
                new FilteredImageSource(image.getSource(),
                        cropFilter));
        BufferedImage tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.drawImage(img, 0, 0, new_w, new_h, null); //  绘制切割后的图
        g.dispose();
        ImageIO.write(tag, "jpg", new File("C:/Users/15804/finalproject.jpg"));
        return tag;
    }
    public final static int[][] cut2( BufferedImage bi,int rows, int cols) {
        int InputMatrix[][]=new int[rows][cols];
        try {
            int srcWidth = bi.getHeight();
            int srcHeight = bi.getWidth();
            if (srcWidth > 0 && srcHeight > 0) {
                Image img;
                ImageFilter cropFilter;
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                int destWidth = srcWidth;
                int destHeight = srcHeight;
                if (srcWidth % cols == 0) {
                    destWidth = srcWidth / cols;
                } else {
                    destWidth = (int) Math.floor(srcWidth / cols) + 1;
                }
                if (srcHeight % rows == 0) {
                    destHeight = srcHeight / rows;
                } else {
                    destHeight = (int) Math.floor(srcWidth / rows) + 1;
                }
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        cropFilter = new CropImageFilter(j * destWidth, i * destHeight,
                                destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                        cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null); //  绘制缩小后的图
                        g.dispose();
                        if(IsBlank(tag,destWidth,destHeight)==true){
                            InputMatrix[i][j]=1;
                        }
                        else{
                            InputMatrix[i][j]=0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return InputMatrix;
    }
    public final static boolean IsBlank(BufferedImage tag,int destWidth,int destHeight){
        boolean blank=true;
        int gray[][]=new int[destWidth][destHeight];
        for (int x = 0; x < destWidth; x++) {
            for (int y = 0; y < destHeight; y++) {
                gray[x][y]=getGray(tag.getRGB(x, y));
            }
        }
        for(int i=0;i<destWidth&&blank==true;i++){
            for(int j=0;j<destHeight&&blank==true;j++){
                if(gray[i][j]==0){
                    blank=false;
                }
            }
        }
        return blank;
    }
    public final static int getGray(int rgb){
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
}
