
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
public class BufferedImageBlock extends Component{
    
    private BufferedImage bi;
    
    public BufferedImageBlock(float hue) {
        
        try {
            bi = ImageIO.read(new File(getClass().getResource("TetrisBlock.png").toURI()));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(BufferedImageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setHueSaturation(bi, hue, 1);
        
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
    
    @Override
    public void paint(Graphics g){
        g.drawImage(bi, 10, 10, null);
    }
}
