package TetrisBlock;


import gui.Field;
import gui.Gui;
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
    private float hue;
    
    public Block(int xLoc, int yLoc, float hue) {
        try {
            bi = ImageIO.read(getClass().getResource("/TetrisBlock/TetrisBlock.png"));
        } catch (IOException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setHueSaturation(bi, hue, 1);
        this.hue=hue;
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
        g.drawImage(bi, bi.getWidth()*loc.x+Field.B_WIDTH,
                bi.getWidth()*(Field.F_HEIGHT-loc.y+1), null);
    }
    
    public Point getTranslateLoc(int dx, int dy){
        return new Point(loc.x+dx, loc.y+dy);
    }
    
    public void translate(int dx, int dy){
        loc.x+=dx;
        loc.y+=dy;
    }
    
    public Point getLoc(){
        return new Point(loc);
    }
    
    public String toString(){
        return loc.toString();
    }
}