/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import TetrisBlock.Block;
import TetrisBlock.Polyomino;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Jonathon
 */
public class Field extends Component{
    public static final int F_WIDTH = 10;
    public static final int F_HEIGHT = 20;
    public static final int B_WIDTH = 24;
    private Block[][] contents;
    private BufferedImage bi;
    
    public Field(){
        contents = new Block[F_HEIGHT+Gui.MAX_PSIZE][F_WIDTH];
        generateBufferedImage();
    }
    
    public void takePolyomino(Polyomino p){
        for (Block b:p.getContents()){
            if (contents[b.getLoc().y][b.getLoc().x]==null){
                contents[b.getLoc().y][b.getLoc().x] = b;
            }
        }
        p=null;
        
        //Clearing rows:
        for (int i=contents.length-1; i>=0; i--){
            boolean fullRow=true;
            for (Block b:contents[i]){
                fullRow = fullRow && b!=null;
            }
            if (fullRow){
                Block[][] temp = new Block[contents.length][contents[0].length];
                System.arraycopy(contents, 0, temp, 0, i);
                System.arraycopy(contents, i+1, temp, i, contents.length-i-1);
                contents=temp;
                for (int y=0; y+i<contents.length; y++){
                    for (Block b:contents[y+i]){
                        if (b!=null) b.translate(0, -1);
                    }
                }
            }
        }
    }
    
    public boolean rotate(Polyomino poly){
        //Allows for wall kicks
        for (int i=0; i<=poly.getLength()/2; i=(i<=0)?(-i+1):(-i)){
            boolean canRotate=true;
            for (Point loc:poly.getRotateAndTranslateLocs(i,0)){
                if (loc.y<0 || loc.x<0 || loc.x>=F_WIDTH ||
                        contents[loc.y][loc.x] != null){
                    canRotate=false;
                }
            }
            if (canRotate){
                poly.translate(i, 0);
                poly.rotate();
                return true;
            }
        }
        return false;
    }
    
    public boolean translate(Polyomino poly, int dx, int dy){
        for (Point loc:poly.getTranslateLocs(dx, dy)){
            if (loc.y<0 || loc.x<0 || loc.x>=F_WIDTH ||
                    contents[loc.y][loc.x] != null) return false;
        }
        poly.translate(dx, dy);
        return true;
    }
    
    public boolean act(Polyomino p){
        if (translate(p, 0, -1)) return true;
        takePolyomino(p);
        return false;
    }
    
    @Override
    public void paint(Graphics g){
        for (Block[] bls:contents){
            for (Block b:bls){
                if (b!=null){
                    b.paint(g);
                }
            }
        }
        g.drawImage(bi, 0, 0, this);
    }
    
    public void generateBufferedImage(){
        bi = new BufferedImage(Field.F_WIDTH*24+2*Field.B_WIDTH,
                (Field.F_HEIGHT+2)*24+Field.B_WIDTH, BufferedImage.TYPE_INT_ARGB);
        BufferedImage b=null;
        try {
            b = ImageIO.read(getClass().getResource("/TetrisBlock/TetrisBlock.png"));
        } catch (IOException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        }
        Graphics2D g = bi.createGraphics();
        for (int i=0; i<(Field.F_WIDTH*24+2*Field.B_WIDTH); i+=24){
            g.drawImage(b, i, (Field.F_HEIGHT)*24+2*Field.B_WIDTH, this);
        }
        for (int i=0; i<(Field.F_HEIGHT+2)*24+Field.B_WIDTH; i+=24){
            g.drawImage(b, 0, i, this);
            g.drawImage(b, Field.F_WIDTH*24+Field.B_WIDTH, i, this);
        }
    }
}
