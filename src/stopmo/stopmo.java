package stopmo;

/*
 * sample java application for camera remote testing
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class stopmo extends JFrame implements CamSocketServerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8505936724035716117L;
	static boolean initialized;
	private CameraView cameraview;

	private CamSocketServer server;

	private File projectdir= new File("d:\\work\\stopproj");
	
	public stopmo() throws HeadlessException {
		super("stopmo");
		initialize();
		setSize(496, 246);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public stopmo(GraphicsConfiguration arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public stopmo(String arg0) throws HeadlessException {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public stopmo(String arg0, GraphicsConfiguration arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		initialized = false;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println("Unable to load native look and feel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				stopmo thisClass = new stopmo();
				//thisClass.setPreferredSize(new Dimension(600,600));
				thisClass.setSize(700,500);
				thisClass.setVisible(true);
			}
		});

	}

	private void initialize() {
		/*
        System.getProperties().put( "proxySet", "true" );
        System.getProperties().put( "proxyHost", "localhost" );
        System.getProperties().put( "proxyPort", "8123" );
		 */

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		JMenuBar menuBar = buildMenuBar();
		setJMenuBar(menuBar);

		cameraview = new CameraView(this);
		cameraview.setPreferredSize(new Dimension(700,500));
		/*
		JScrollPane scroll = new JScrollPane(pageview);
		 */

		JPanel pane = new JPanel();
		BorderLayout bord = new BorderLayout();
		pane.setLayout(bord);
		//pane.add("North", toolbar);
		pane.add("Center", cameraview);

		setContentPane(pane);

		// create websocket server 		
		server = new CamSocketServer(new InetSocketAddress("10.24.244.99",5000),5000,this);
		server.start();
		initialized = true;
	}

	private JMenuBar buildMenuBar() {
		JMenuBar menubar = new JMenuBar();

		// build the File menu
		JMenu fileMenu = new JMenu("File");
		JMenuItem openMenuItem = new JMenuItem("Open");

		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayaction("open");
			}
		});		
		fileMenu.add(openMenuItem);

		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
            	fileChooser.setDialogTitle("project directory");
            	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            	fileChooser.setAcceptAllFileFilterUsed(false);
    			int returnValue = fileChooser.showOpenDialog(null);
    			if (returnValue == JFileChooser.APPROVE_OPTION) {
    				projectdir = fileChooser.getCurrentDirectory();
    			}
			}
		});
		fileMenu.add(newMenuItem);

		/*
		JMenuItem close = xp.getCloseMenu();
		JMenuItem save = xp.getSaveMenu();
		 */

		fileMenu.add(new JSeparator());
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (quitConfirmed(stopmo.this)) {
					System.exit(0);
				}
			}
		});
		fileMenu.add(quitMenuItem);

		menubar.add(fileMenu);	

		// build the Edit menu
		JMenu editMenu = new JMenu("Edit");
		JMenuItem cutMenuItem = new JMenuItem("Cut");
		JMenuItem copyMenuItem = new JMenuItem("Copy");
		JMenuItem pasteMenuItem = new JMenuItem("Paste");
		editMenu.add(cutMenuItem);
		editMenu.add(copyMenuItem);
		editMenu.add(pasteMenuItem);

		menubar.add(editMenu);

		/*

		JMenuItem prefs = xp.getPreferencesMenu();
		// create the pref dialog ...
		pref = new PreferencesDialog(this,false);
		prefs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("prefs");                         
				//pref.pack(); // bad size ...
				pref.setVisible(true);

			}
		});
		// only manually add on non-mac
		if(!xp.isMac()) {
			file.add(new JSeparator());
			file.add(prefs);
		}
		 */

		/*
		ActionListener close_list = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//hide();
				dispose();
			}
		};
*/
		/*
		neww.addActionListener(new_list);

		JMenu document = new JMenu("Document");
		JMenuItem newframe = new JMenuItem("New Frame");
		document.add(newframe);
		newframe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFrame();
			}
		});

		JMenuItem newimage = new JMenuItem("New Image");
		document.add(newimage);

		ActionListener newimage_list = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newImage();
			}
		};

		newimage.addActionListener(newimage_list);
		menubar.add(document);
		 */
		return menubar;
	}

	// ============== some methods ====
	private boolean quitConfirmed(JFrame frame) {
		String s1 = "Quit";
		String s2 = "Cancel";
		Object[] options = {s1, s2};
		int n = JOptionPane.showOptionDialog(frame,
				"There is unsaved work.\nDo you really want to quit?",
				"Quit Confirmation",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				s1);
		if (n == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	// dummy action
	public void displayaction(String action)
	{
		System.out.println("action : "+action);
	}

	// deal with new camera and view !
	@Override
	public void addCamera(CameraModel camera) {				
		cameraview.setCamera(camera);
		camera.attach(cameraview);
	}

	@Override
	public void delCamera(CameraModel camera) {
		camera.detach(cameraview);
		cameraview.setCamera(null);	
	}
	
	private File getShotFile() {
		//take the current timeStamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		
		/* project dir */
		mediaFile = new File(projectdir + File.separator + "DSC_" + timeStamp + ".jpg");
		return mediaFile;
	}

	public void saveShot(byte[] shot) {
		System.out.println("saveShot");
		// save file
		File outputfile = getShotFile();
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(outputfile);
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		}
		try {
			stream.write(shot);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		System.out.println("saveShot: save in" + outputfile.getAbsolutePath());
	}
	
}
