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
			img = toGrayScale (img);
			display(img);
			
			
		}
	}

	// Display image in a JPanel pop-up
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
	public static BufferedImage toGrayScale (BufferedImage img) {
		System.out.println(" Converting to GrayScale.");
		BufferedImage grayImage = new BufferedImage(
				img.getWidth(),img.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = grayImage.getGraphics();
		g.drawImage(img,  0, 0, null);
		g.dispose();
		
		
		return grayImage;
	}
	public static BufferedImage toGrayScale2 (BufferedImage img) {
		System.out.println(" Converting to GrayScale2.");
		BufferedImage grayImage = new BufferedImage(
				img.getWidth(),img.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
		int rgb = 0, r=0, g=0, b=0;
		for(int y=0;y<img.getHeight();y++) {
			for(int x=0; x<img.getWidth();x++)// nested loops to go pixel by pixel in the pixel array
				{
				rgb =(int)(img.getRGB(x,y));
				r = ((rgb >> 16) & 0xFF); // this three lines get the Red Green and Blue values from each pixel
				g = ((rgb >> 8) & 0xFF);
				b = (rgb & 0xFF);
				rgb = (int)((r+g+b)/3); // sums the value and divides it by 3 to make the average of the value
				// rgb = (int) (0.299*r + 0.587*g + 0.114 * b) ; // would be the same line as above but in weighted average by opticians
				rgb = (255<<24) | (rgb<<16) | (rgb<<8) | rgb; // red, green and blue respectively
				grayImage.setRGB(x, y, rgb);
			}
		}
		return grayImage;
	}
	
}
