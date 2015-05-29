package stopmo;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;


public class CameraView extends JPanel  implements CameraModelListener {

	/**
	 * 
	 */	
	private static final long serialVersionUID = 2674244984196311672L;

	//TODO:
	private stopmo project;

	private CameraModel camera;

	private JButton takeShotBtn;

	private BufferedImage previewFrame; 
	private BufferedImage lastShot;

	final static BasicStroke stroke = new BasicStroke(2.0f);
	private float alpha = 0.5f;
	private AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);

	private Dimension scaled;

	private long timestamp=0;
	
	/*
	 * calculate the correct ratio for preview display
	 */
	private Dimension preserveRatio(Dimension img) {
		Dimension frame = getSize();

		double resizedHeight;
		double resizedWidth;

		double aspect = img.getWidth() / img.getHeight();

		if(frame.getHeight() < frame.getWidth()) {
			resizedHeight = frame.getHeight();
			resizedWidth =  (resizedHeight * aspect);
		}

		else { // screen width is smaller than height (mobile, etc)
			resizedWidth = frame.getWidth();
			resizedHeight =  (resizedWidth / aspect);      
		}
		Dimension resized = new Dimension();
		resized.setSize(resizedWidth, resizedHeight);
		return resized;
	}

	CameraView(stopmo stopmo) {
		takeShotBtn = new JButton("Shot");
		takeShotBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (camera != null)
					camera.takeShot();	
			}
		});		
		add(takeShotBtn);
		
		camera = null;

		lastShot = null;
		previewFrame = null;

		//TODO:
		project = stopmo;
	}

	@Override
	public void paintComponent(Graphics g){	
		Graphics2D g2d = (Graphics2D) g;		

		Composite originalComposite = g2d.getComposite();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);


		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, getWidth(), getHeight() );

		if (lastShot != null) {
			int h = lastShot.getHeight(null);
			int w = lastShot.getWidth(null);
			g2d.drawImage(lastShot, 
					0,0,w,h, // dest
					0,0,w,h, // src
					null);
		}
		if (previewFrame != null) {
			// draw current frame with an alpha
			g2d.setComposite(ac);
							
			int h = previewFrame.getHeight(null);
			int w = previewFrame.getWidth(null);

			g2d.drawImage(previewFrame, 
					0,0,scaled.width,scaled.height, // dest
					0,0,w,h, // src
					null);
		} 

		// draw HD frame
		int hHD = 9* getWidth() / 16;
		int delta = (getHeight() - hHD)/2;

		g2d.setPaint(Color.magenta);
		g2d.setStroke(stroke);

		Rectangle2D HDRect = new Rectangle(0,delta,getWidth(),hHD);

		g2d.draw(HDRect);
		/*
		g2d.setColor(Color.blue);
		g2d.drawLine(30, 30, 80, 80);
		g2d.drawRect(20, 150, 100, 100);
		g2d.fillRect(20, 150, 100, 100);
		 */

		g2d.setComposite(originalComposite);
	}  

	private BufferedImage getScaledImage(BufferedImage src){
		BufferedImage resizedImg = new BufferedImage(scaled.width,scaled.height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(src, 0, 0, scaled.width,scaled.height, null);
	    g2.dispose();
	    return resizedImg;
	}
	
	@Override
	public void onShot(byte[] img) {		
		if (project != null)
			project.saveShot(img);

		// convert to image for display
		InputStream in = new ByteArrayInputStream(img);
		try {
			// store a small version for preview
			BufferedImage fulllastShot = ImageIO.read(in);
			lastShot = getScaledImage(fulllastShot);
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		repaint();
	}

	@Override
	public void onPreview(BufferedImage img) {
		System.out.println("onPreview Time Gap = "+(System.currentTimeMillis()-timestamp));
		timestamp=System.currentTimeMillis();
		
		previewFrame = img;
		repaint();

	}

	public void setCamera(CameraModel camera) {
		this.camera = camera;		
		//System.out.println("attaching new camera :" + camera.getModel());
	}

	@Override
	public void onPreviewSize(Dimension psize) {	
		System.out.println("onPreviewSize, preview is : " + psize);

		scaled = preserveRatio(psize);		
	}
}

