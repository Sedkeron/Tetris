package TetrisBlock;


import Temp.Field;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathon
 */
public class Block extends Component{
    
    private BufferedImage bi;
    private Point loc;
    
    public static final int DIR_DOWN=0;
    public static final int DIR_UP=1;
    public static final int DIR_LEFT=2;
    public static final int DIR_RIGHT=3;
    
    public Block(int xLoc, int yLoc, float hue) {
        try {
            bi = ImageIO.read(getClass().getResource("TetrisBlock.png"));
        } catch (IOException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setHueSaturation(bi, hue, 1);
        
        this.loc=new Point(xLoc, yLoc);
    }
        
    private void setHueSaturation(BufferedImage bi, float hue, float sat){        
        for (int x=0; x<bi.getWidth(); x++){
            for (int y=0; y<bi.getHeight(); y++){
                int rgba = bi.getRGB(x, y);
                Color c= new Color(rgba, false);
                int aCompensation=rgba-c.getRGB();
                float[] hsbVals=Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                c = new Color(Color.HSBtoRGB(hue, sat, hsbVals[2]), true);
                bi.setRGB(x, y, c.getRGB()+aCompensation);
            }
        }
    }
    
    public BufferedImage getImage(){
        return bi;
    }
    
    @Override
    public void paint(Graphics g){
        g.drawImage(bi, bi.getWidth()*loc.x, bi.getWidth()*(Field.HEIGHT-loc.y), null);
    }
    
    public void shift(int direction){
        if (direction == DIR_DOWN) loc.translate(0, -1);
        else if (direction == DIR_UP) loc.translate(0,1);
        else if (direction == DIR_LEFT) loc.translate(-1,0);
        else if (direction == DIR_RIGHT) loc.translate(1,0);
    }
    
    public void rotateCW(Point loc){
        int dx = this.loc.y-loc.y;
        int dy = loc.x-this.loc.x;
        this.loc=new Point(loc.x+dx, loc.y+dy);
    }
    
    public void rotateCCW(Point loc){
        int dx = loc.y-this.loc.y;
        int dy = this.loc.x-loc.x;
        this.loc=new Point(loc.x+dx, loc.y+dy);
    }
    
    public Point getLoc(){
        return new Point(loc);
    }
}