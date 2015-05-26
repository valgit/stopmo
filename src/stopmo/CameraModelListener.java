package stopmo;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public interface CameraModelListener {
	public void onShot(byte[] img);
	public void onPreview(BufferedImage img);
	public void onPreviewSize(Dimension psize);
}
