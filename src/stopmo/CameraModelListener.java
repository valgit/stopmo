package stopmo;

import java.awt.image.BufferedImage;

public interface CameraModelListener {
	public void onShot(BufferedImage img);
	public void onPreview(BufferedImage img);
}
