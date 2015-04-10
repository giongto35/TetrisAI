import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;



public class TFrame extends JFrame implements KeyListener{
	private static final long serialVersionUID = 1L;
	public TLabel label = new TLabel(300,700);
	public State s;
	
	public int orient, slot;
	
	public static final int MANUAL = 0;
	public static final int NONE = 1;
	
	public int mode = MANUAL;
	
	//constructor
	public TFrame (State s){
		this.s = s;
		s.label = label;
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows when this is closed
		setTitle("Tetris BKW");
		setContentPane(label.draw);
		pack();
		label.BORDER = .05;
		label.setXscale(0, State.COLS);
		label.setYscale(0, State.ROWS+5);
		this.addKeyListener(this);  //may be unnecessary (not certain)
		setVisible(true);
	}
	
	//switches which state is attached to this TFrame
	public void bindState(State s) {
		if(s!= null)	s.label = null;
		this.s = s;
		s.label = label;
	}
	
	///
	/// ADDED BY DON (AKA Pimp Masta) 1/22/09
	///
	public TFrame (){
		s.label = label;
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows when this is closed
		setTitle("Eric Whitman's Tetris Simulator");
		setContentPane(label.draw);
		pack();
		label.BORDER = .05;
		label.setXscale(0, State.COLS);
		label.setYscale(0, State.ROWS+5);
		this.addKeyListener(this);  //may be unnecessary (not certain)
		setVisible(true);
	}

	public void keyPressed(KeyEvent e) {
		switch(mode) {
			case(MANUAL): {
				switch(e.getKeyCode()) {
					case(KeyEvent.VK_RIGHT): {
						if(slot < State.COLS-State.pWidth[s.nextPiece][orient])	slot++;
						s.clearNext();
						s.drawNext(slot, orient);
						break;
					}
					case(KeyEvent.VK_LEFT): {
						if(slot > 0)	slot--;
						s.clearNext();
						s.drawNext(slot, orient);
						break;
					}
					case(KeyEvent.VK_UP): {
						orient++;
						if(orient%State.pOrients[s.nextPiece]==0)	orient = 0;
						if(slot > State.COLS-State.pWidth[s.nextPiece][orient])
							slot = State.COLS-State.pWidth[s.nextPiece][orient];
						s.clearNext();
						s.drawNext(slot, orient);
						break;
					}
					case(KeyEvent.VK_DOWN): {
						if(!s.makeMove(orient, slot))	mode = NONE;
						if(orient >= State.pOrients[s.nextPiece])	orient = 0;
						if(slot > State.COLS-State.pWidth[s.nextPiece][orient])
							slot = State.COLS-State.pWidth[s.nextPiece][orient];
						
						s.draw();
						if(mode == NONE)	{
							label.text(State.COLS/2.0, State.ROWS/2.0, "You Lose");
						}
						s.clearNext();
						s.drawNext(slot, orient);
						break;
					}
					default:
						break;
				}
			}
			case(NONE):	break;
			default:
				System.out.println("unknown mode");
				break;
		}
		
		
		
		
	}


	public void keyReleased(KeyEvent e) {
	}


	public void keyTyped(KeyEvent e) {

	}
	
    public void save(String filename) {
        File file = new File(filename);
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);


        BufferedImage bImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphic = (Graphics2D)bImage.getGraphics();
        paint(graphic);
        graphic.drawImage(bImage, 0, 0, null);
//         png files
        if (suffix.toLowerCase().equals("png")) {
            try { ImageIO.write(bImage, suffix, file); }
            catch (IOException e) { e.printStackTrace(); }
        }
        else System.out.println("unknown extension");
    }
	
	public static void main(String[] args) {
		State s = new State();
		TFrame t = new TFrame(s);
		s.draw();
		s.drawNext(0,0);
		//t.save("picture.png");
		
	}
}
