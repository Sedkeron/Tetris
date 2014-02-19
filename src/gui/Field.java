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
import java.awt.RenderingHints;
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
    public static int fWidth;
    public static int fHeight;
    public static final int B_SIZE=24;
    private Block[][] contents;
    private BufferedImage bi;
    
    //User should only have one Field in use at a time
    
    public Field(int fWidth, int fHeight){
        this.fWidth=fWidth;
        this.fHeight=fHeight;
        contents = new Block[fHeight+fWidth][fWidth];
        try {
            generateBufferedImage();
        } catch (IOException ex) {
            Logger.getLogger(Field.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //returns the number of lines cleared
    public int takePolyomino(Polyomino p){
        for (Block b:p.getContents()){
            if (contents[b.getLoc().y][b.getLoc().x]==null){
                contents[b.getLoc().y][b.getLoc().x] = b;
            }
        }
        p=null;
        int linesCleared=0;
        //Clearing rows:
        for (int i=contents.length-1; i>=0; i--){
            boolean fullRow=true;
            for (Block b:contents[i]){
                fullRow = fullRow && b!=null;
            }
            if (fullRow){
                linesCleared++;
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
        return linesCleared;
    }
    
    public boolean rotate(Polyomino poly){
        //Allows for wall kicks
        for (int i=0; i<=poly.getLength()/2; i=(i<=0)?(-i+1):(-i)){
            boolean canRotate=true;
            for (Point loc:poly.getRotateAndTranslateLocs(i,0)){
                if (loc.y<0 || loc.x<0 || loc.x>=fWidth ||
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
            if (loc.y<0 || loc.x<0 || loc.x>=fWidth ||
                    contents[loc.y][loc.x] != null) return false;
        }
        poly.translate(dx, dy);
        return true;
    }
    
    public boolean act(Polyomino p){
        if (translate(p, 0, -1)) return true;
        return false;
    }
    
    public boolean lose(){
        boolean lose=false;
        for (Block b:contents[fHeight]){
            lose = lose || b!=null;
        }
        return lose;
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
    
    public void generateBufferedImage() throws IOException{
        BufferedImage biL, biBL, biB, biBR, biR, biTR, biT, biTL;
        biL = ImageIO.read(getClass().getResource("/gui/TetrisFieldLeft.png"));
        biBL = ImageIO.read(getClass().getResource("/gui/TetrisFieldBottomLeft.png"));
        biB = ImageIO.read(getClass().getResource("/gui/TetrisFieldBottom.png"));
        biBR = ImageIO.read(getClass().getResource("/gui/TetrisFieldBottomRight.png"));
        biR = ImageIO.read(getClass().getResource("/gui/TetrisFieldRight.png"));
        biTR = ImageIO.read(getClass().getResource("/gui/TetrisFieldTopRight.png"));
        biT = ImageIO.read(getClass().getResource("/gui/TetrisFieldTop.png"));
        biTL = ImageIO.read(getClass().getResource("/gui/TetrisFieldTopLeft.png"));
        
        bi = new BufferedImage((fWidth+2)*B_SIZE, (fHeight+2)*B_SIZE, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        g.drawImage(biTL, 0, 0, B_SIZE, B_SIZE, 0, 0, biTL.getWidth(), biTL.getHeight(), null);
        g.drawImage(biBL, 0, B_SIZE*(fHeight+1), B_SIZE, B_SIZE*(fHeight+2), 0, 0, biTL.getWidth(), biTL.getHeight(), null);
        g.drawImage(biTR, B_SIZE*(fWidth+1), 0, B_SIZE*(fWidth+2), B_SIZE, 0, 0, biTL.getWidth(), biTL.getHeight(), null);
        g.drawImage(biBR, B_SIZE*(fWidth+1), B_SIZE*(fHeight+1), B_SIZE*(fWidth+2), B_SIZE*(fHeight+2), 0, 0, biTL.getWidth(), biTL.getHeight(), null);
        g.drawImage(biL, 0, B_SIZE, B_SIZE, B_SIZE*(fHeight+1), 0, 0, biL.getWidth(), biL.getHeight(), null);
        g.drawImage(biR, B_SIZE*(fWidth+1), B_SIZE, B_SIZE*(fWidth+2), B_SIZE*(fHeight+1), 0, 0, biR.getWidth(), biR.getHeight(), null);
        g.drawImage(biT, B_SIZE, 0, B_SIZE*(fWidth+1), B_SIZE, 0, 0, biT.getWidth(), biT.getHeight(), null);
        g.drawImage(biB, B_SIZE, B_SIZE*(fHeight+1), B_SIZE*(fWidth+1), B_SIZE*(fHeight+2), 0, 0, biB.getWidth(), biB.getHeight(), null);
        
        
        g.dispose();
    }
}
