/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import PolyominoIO.PolyominoInputStream;
import TetrisBlock.Polyomino;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author Jonathon
 */
public class Gui {
    public static final int MAX_PSIZE = 8;
    
    private static Polyomino curP;
    private static Field field;
    private static JFrame frame;
    private static JPanel mainPanel;
    private static Action upAction, leftAction, rightAction, spaceAction, downAction;
    private static long waitTime;
    private static boolean running;
    private static BufferedImage foreground;
    private static final Object LOCK = new Object();
    
    public Gui(){}
    
    public static void main(String[] args) {
        frame = new JFrame("Tetris");
        frame.add(makePanel());
        frame.setPreferredSize(new Dimension(Field.F_WIDTH*24+2*Field.B_WIDTH+6,
                Field.F_HEIGHT*24+Field.B_WIDTH+28));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        
        waitTime=400;
        running=false;
        setForeground("TetrisStartScreenV1.png");
        
        
        mainPanel.repaint();
        while(true){
            synchronized (LOCK){
                while (!running) try {
                    LOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                mainPanel.repaint();
                Thread.sleep(waitTime/8);
                synchronized (LOCK){
                    LOCK.wait(waitTime*7/8);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!field.act(curP)){
                curP=getRanPoly();
                if (field.lose()){
                    reset();
                }
            }
        }
    }

    private static JPanel makePanel(){
        mainPanel=new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                curP.paint(g);
                field.paint(g);
                if (foreground!=null) g.drawImage(foreground, 0, -24, this);
            }
        };
        upAction = new UpAction();
        leftAction = new LeftAction();
        rightAction = new RightAction();
        spaceAction = new SpaceAction();
        downAction = new DownAction();
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("UP"), "doUpAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("I"), "doUpAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("W"), "doUpAction");
        mainPanel.getActionMap().put("doUpAction", upAction);
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "doLeftAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("J"), "doLeftAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("A"), "doLeftAction");
        mainPanel.getActionMap().put("doLeftAction", leftAction);
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "doRightAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("L"), "doRightAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("D"), "doRightAction");
        mainPanel.getActionMap().put("doRightAction", rightAction);
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "doDownAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("K"), "doDownAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("S"), "doDownAction");
        mainPanel.getActionMap().put("doDownAction", downAction);
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "doSpaceAction");
        mainPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK), "doSpaceAction");
        mainPanel.getActionMap().put("doSpaceAction", spaceAction);
        mainPanel.setPreferredSize(new Dimension(300,500));
        curP=getRanPoly();
        field=new Field();
        mainPanel.add(curP);
        mainPanel.add(field);

        return mainPanel;
    }
        
    private static class UpAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent up){
            synchronized (LOCK){
                if (!running) return;
                field.rotate(curP);
                mainPanel.repaint();
            }
        }
    }
    private static class LeftAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent left){
            synchronized (LOCK){
                if (!running) return;
                field.translate(curP,-1,0);
                mainPanel.repaint();
            }
        }
    }
    private static class RightAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent right){
            synchronized (LOCK){
                if (!running) return;
                field.translate(curP,1,0);
                mainPanel.repaint();
            }
        }
    }
    private static class DownAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent down){
            synchronized (LOCK){
                LOCK.notifyAll();
            }
        }
    }
    private static class SpaceAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent space){
            synchronized (LOCK){
                if (!running){
                    running=true;
                    foreground=null;
                    LOCK.notifyAll();
                    return;
                }
                mainPanel.repaint();
                while (field.translate(curP,0,-1));
                field.takePolyomino(curP);
                curP = getRanPoly();
                if (field.lose()){
                    reset();
                }
            }
        }
    }
    
    
    private static Polyomino getRanPoly(){
        int s = (int)(Math.random()*6)+1;
        PolyominoInputStream i = new PolyominoInputStream(s);
        ArrayList<boolean[][]> all = i.readAll();
        
        int n = (int)(Math.random()*all.size());
        
        
        return new Polyomino(Field.F_WIDTH/2, Field.F_HEIGHT+3, all.get(n), (float)n/all.size());
    }
    
    private static void setForeground(String name){
        try {
            foreground=ImageIO.read(new Gui().getClass().getResource("/gui/"+name));
        } catch (IOException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void reset(){
        synchronized (LOCK){
            running=false;
            setForeground("TetrisStartScreenV1.png");
            mainPanel.repaint();
            field = new Field();
        }
    }
}
