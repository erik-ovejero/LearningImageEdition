import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;

public class ImageFilter {
	public static void main(String[] args) {
		File file = new File("src/2222.png"); // new object file to upload the image
		BufferedImage img = null;
		
		try { img = ImageIO.read(file); }
		catch (IOException e) {e.printStackTrace(System.out);}
		
		if (img!=null) {
			display(img);
			
		}
	}

	// Display image in a JPanel popup
	public static void display (BufferedImage img) {
		System.out.println(" Displaying image.");
		JFrame frame = new JFrame();
		JLabel label = new JLabel();
		frame.setSize(img.getWidth(),img.getHeight()); // set frame to the same size as the Image
		label.setIcon(new ImageIcon(img)); // set the label as the image itself
		frame.getContentPane().add(label, BorderLayout.CENTER); // center the image (label) to the frame
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //terminates the application when closing the window
		frame.pack();
		frame.setVisible(true);
		
	}
}
