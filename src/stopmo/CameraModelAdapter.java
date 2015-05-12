package stopmo;

import java.awt.image.BufferedImage;

/*
 * pure virtual for listener
 */
public abstract class CameraModelAdapter implements CameraModelListener {

	public void onShot(BufferedImage img) { }
	public void onPreview(BufferedImage img) {}

}
