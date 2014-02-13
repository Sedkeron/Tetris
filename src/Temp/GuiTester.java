package Temp;


import PolyominoIO.PolyominoInputStream;
import TetrisBlock.Polyomino;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JApplet;
import javax.swing.JFrame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathon
 */
public class GuiTester extends JApplet{
    private Polyomino p;
    
    public GuiTester(){
        PolyominoInputStream i = new PolyominoInputStream(4);
        ArrayList<boolean[][]> all = i.readAll();
        
        p=new Polyomino(10, 10, all.get((int)(Math.random()*all.size())), new Random().nextFloat());
    }
    
    public void buildUI(){
        add(p);
        //only does the last; comment out add(p2); to see the first
    }
    
    public static void main(String[] args) {
        JFrame f = new JFrame("Buffered Image Test");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        
        GuiTester tst = new GuiTester();
        tst.buildUI();
        f.add(tst);
        
        f.setPreferredSize(new Dimension(500, 500));
        
        f.pack();
        f.setVisible(true);
        
        while(true){
            long t=System.currentTimeMillis();
//            while (System.currentTimeMillis()-t<200){}
//            tst.p.shift(Polyomino.DIR_LEFT);
//            tst.p.repaint();
//            while (System.currentTimeMillis()-t<400){}
//            tst.p.shift(Polyomino.DIR_UP);
//            tst.p.repaint();
//            while (System.currentTimeMillis()-t<600){}
//            tst.p.shift(Polyomino.DIR_RIGHT);
//            tst.p.repaint();
//            while (System.currentTimeMillis()-t<800){}
//            tst.p.shift(Polyomino.DIR_DOWN);
//            tst.p.repaint();
            while (System.currentTimeMillis()-t<1000){}
            tst.p.rotate();
            tst.p.repaint();
        }
    }
}
