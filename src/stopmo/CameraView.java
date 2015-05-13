package stopmo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CameraView extends JPanel  implements CameraModelListener {

	/**
	 * 
	 */	
	private static final long serialVersionUID = 2674244984196311672L;

	private CameraModel camera;

	private JButton snap;

	private BufferedImage currentShot; 

	CameraView() {
		/*
		  JButton takeShotBtn = new JButton("Shot");	
		  
	      setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
	      add(inputField);
	      add(takeShotBtn);
	      */
		currentShot = null;
		camera = null;
		// TODO: create snap button
	}
	public void paintComponent(Graphics g){	
		//System.out.println("Je suis exécutée !"); 
		//g.fillOval(20, 20, 75, 75);		
		if (currentShot != null) {
			System.out.println("currentShot");
			g.drawImage(currentShot, 0,0,this);	
		}
		//TODO: other drawing
	}  

	@Override
	public void onShot(BufferedImage img) {		
		System.out.println("onShot: new img");
		int width = img.getWidth();
		int height = img.getHeight();
		System.out.println("onShot: size: "+width+" h: "+height);
		currentShot = img;
		repaint();
		// TODO: should call document meth ?
	}

	@Override
	public void onPreview(BufferedImage img) {
		// TODO Auto-generated method stub
		System.out.println("onPreview: new img");
		repaint();

	}
	
	public void setCamera(CameraModel camera) {
		this.camera = camera;		
	}

}

