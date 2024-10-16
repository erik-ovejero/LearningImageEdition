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
			//img = toGrayScale (img);
			img = toGrayScale2 (img);
			//display(img);
			//img = pixelate(img);
			// the second value in pixelate 2 being the amount of pixels in x and y that get pixelated
			//img = pixelate2(img,10);
			//img = resize( img,250);
			//img = blur(img);
			img = blur(blur(img));
			//img = brighten (img,40);
			//img = heavyblur(img);
			img = detectEdges (img);
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
	// methods to convert image to grayscale
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
	// method to apply 2x2 pixelation to a grayscale image
	public static BufferedImage pixelate (BufferedImage img) {
		BufferedImage pixImg = new BufferedImage(
				img.getWidth(),img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		int pix=0, p=0;
		// this next for loop iterates through the y and x in steps of 2
		for(int y=0; y<img.getHeight()-2;y+=2) {
			for (int x=0; x<img.getWidth()-2;x+=2) {
				// turns the variable pix into the average of the  2x2 pixels in the array
				pix = (int)((img.getRGB(x, y)& 0xFF)
				+ (img.getRGB(x+1, y)& 0xFF)
				+ (img.getRGB(x, y+1)& 0xFF)
				+ (img.getRGB(x+1, y+1)& 0xFF))/4;
				p = (255<<24) | (pix<<16) | (pix<<8) | pix;
				// set pixel value to each one of the 4 pixels
				pixImg.setRGB(x, y, p);
				pixImg.setRGB(x+1, y, p);
				pixImg.setRGB(x, y+1, p);
				pixImg.setRGB(x+1, y+1, p);
			}
		}
		return pixImg;		
	}

	public static BufferedImage pixelate2 (BufferedImage img, int n) {
		BufferedImage pixImg = new BufferedImage(
				img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0, p=0;
		for(int y = 0; y<img.getHeight()-n; y+=n) {
			for(int x=0; x<img.getWidth() -n; x+=n) {
				for (int a=0; a<n; a++) {
					for (int b=0; b<n; b++) {
						pix += (img.getRGB(x+a,  y+b)& 0xFF);
					}
				}
				pix = (int) (pix/n/n);
				for(int a=0; a<n; a++) {
					for (int b=0; b<n; b++) {
						p = (255<<24) | (pix<<16) | (pix<<8) | pix;
						pixImg.setRGB(x+a, y+b,p);
					}
				}
				pix=0;
			}
		}
		return pixImg;
	}
	// rescale image to selected pixel size for height (keeps proportion)
	public static BufferedImage resize (BufferedImage img, int newHeight) {
		System.out.println(" Scaling image.");
		//calculates the percentage in difference between the height we desire and the height to apply the multiplication to the width as well and keep the proportion
		double scaleFactor= (double) newHeight/img.getHeight();
		//creates instance that becomes the image rescaled
		BufferedImage scaledImg = new BufferedImage(
				(int)(scaleFactor*img.getWidth()), newHeight, BufferedImage.TYPE_BYTE_GRAY);
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return scaleOp.filter(img, scaledImg);
	}
	// apply Gaussian Blur to a grayscale image
	public static BufferedImage blur (BufferedImage img) {
		BufferedImage blurImg = new BufferedImage(
			img.getWidth()-2, img.getHeight()-2, BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0;
		for( int y=0; y<blurImg.getHeight(); y++) {
			for (int x=0; x<blurImg.getWidth();x++) {
				//average the pixels to the gaussian blur formula being in a 3x3 
				// 4 in the pixel in the middle, 2 in the upper, lower, left and right pixels and 1 on the corner pixels
				pix = (int)(4*(img.getRGB(x+1, y+1)& 0xFF)
				+ 2*(img.getRGB(x+1, y)& 0xFF)
				+ 2*(img.getRGB(x+1, y+2)& 0xFF)
				+ 2*(img.getRGB(x, y+1)& 0xFF)
				+ 2*(img.getRGB(x+2, y+1)& 0xFF)
				+ (img.getRGB(x, y)& 0xFF)
				+ (img.getRGB(x, y+2)& 0xFF)
				+ (img.getRGB(x+2, y)& 0xFF)
				+ (img.getRGB(x+2, y+2)& 0xFF))/16;
				//shifts the values to the corresponding channel in the 32 bit values for a pixel being the first 8 for alpha, then red, green and blue
				int p = (255<<24) | (pix<<16) | (pix<<8) | pix;
				blurImg.setRGB(x, y, p);
			}
		}
		return blurImg;
	}
	// heavy blur! (same as blur but with a bigger range) in x and y coordinates in 5x5
	public static BufferedImage heavyblur (BufferedImage img) {
		BufferedImage blurImg = new BufferedImage(
			img.getWidth()-4, img.getHeight()-4, BufferedImage.TYPE_BYTE_GRAY);
		int pix = 0;
		for (int y=0; y<blurImg.getHeight(); y++) {
			for (int x=0; x<blurImg.getWidth(); x++) {
				pix = (int)(
				10*(img.getRGB(x+3, y+3)& 0xFF)
				+ 6*(img.getRGB(x+2, y+1)& 0xFF)
				+ 6*(img.getRGB(x+1, y+2)& 0xFF)
				+ 6*(img.getRGB(x+2, y+3)& 0xFF)
				+ 6*(img.getRGB(x+3, y+2)& 0xFF)
				+ 4*(img.getRGB(x+1, y+1)& 0xFF)
				+ 4*(img.getRGB(x+1, y+3)& 0xFF)
				+ 4*(img.getRGB(x+3, y+1)& 0xFF)
				+ 4*(img.getRGB(x+3, y+3)& 0xFF)
				+ 2*(img.getRGB(x, y+1)& 0xFF)
				+ 2*(img.getRGB(x, y+2)& 0xFF)
				+ 2*(img.getRGB(x, y+3)& 0xFF)
				+ 2*(img.getRGB(x+4, y+1)& 0xFF)
				+ 2*(img.getRGB(x+4, y+2)& 0xFF)
				+ 2*(img.getRGB(x+4, y+3)& 0xFF)
				+ 2*(img.getRGB(x+1, y)& 0xFF)
				+ 2*(img.getRGB(x+2, y)& 0xFF)
				+ 2*(img.getRGB(x+3, y)& 0xFF)
				+ 2*(img.getRGB(x+1, y+4)& 0xFF)
				+ 2*(img.getRGB(x+2, y+4)& 0xFF)
				+ 2*(img.getRGB(x+3, y+4)& 0xFF)
				+ (img.getRGB(x, y)& 0xFF)
				+ (img.getRGB(x, y+2)& 0xFF)
				+ (img.getRGB(x+2, y)& 0xFF)
				+ (img.getRGB(x+2, y+2)& 0xFF))/74;
				int p = (255<<24) | (pix<<16) | (pix<<8) | pix; 
				blurImg.setRGB(x,y,p);
			}
		}
		return blurImg;
	}	
	// brighten color image by percentage
	public static BufferedImage brighten (BufferedImage img, int percentage) {
		int r=0, g=0, b=0, rgb=0, p=0;
		int amount = (int) (percentage * 255/100); // rgb scale is 0-255 so we transform the number we give to a percentage
		BufferedImage newImage = new BufferedImage(
				img.getWidth(),img.getHeight(), BufferedImage.TYPE_INT_ARGB); 
		for(int y=0; y<img.getHeight(); y++) {
			for (int x=0; x<img.getWidth(); x++) {
				rgb = img.getRGB(x, y);
				r = ((rgb >>16)& 0xFF) + amount;
				g = ((rgb >>8)& 0xFF) + amount;
				b = (rgb& 0xFF) + amount;
				if (r>255) r=255;
				if (r<0) r=0;
				if (g>255) g=255;
				if (g<0) g=0;
				if (b>255) b=255;
				if (b<0) b=0;
				p = (255<<24) | (r<<16) | (g<<8) | b;
				newImage.setRGB(x, y, p);
			}
		}
		return newImage;
	}
	// detect edges of a grayscale image using Sobel algorithm
	// if blur is applied beforehand it gives better results
	public static BufferedImage detectEdges (BufferedImage img) {
		int h = img.getHeight(), w = img.getWidth(), threshold=20, p = 0;
		BufferedImage edgeImg = new BufferedImage(w,h, BufferedImage.TYPE_BYTE_GRAY);
		int[][] vert = new int [w][h];
		int [][] horiz = new int [w][h];
		int[][] edgeWeight = new int [w][h];
		for (int y=1; y<h-1;y++) {
			for (int x=1;x<w-1;x++) {
				vert[x][y] = (int)(img.getRGB(x+1, y-1)& 0xFF + 2*(img.getRGB(x+1, y)& 0xFF) + img.getRGB(x+1, y+1)& 0xFF
						- img.getRGB(x-1, y-1)& 0xFF - 2*(img.getRGB(x-1, y)& 0xFF) - img.getRGB(x-1, y+1)& 0xFF);
				horiz[x][y] =  (int)(img.getRGB(x-1, y+1)& 0xFF + 2*(img.getRGB(x, y+1)& 0xFF) + img.getRGB(x+1, y+1)& 0xFF
						- img.getRGB(x-1, y-1)& 0xFF - 2*(img.getRGB(x, y-1)& 0xFF) - img.getRGB(x+1, y-1)& 0xFF);
				edgeWeight[x][y] = (int)(Math.sqrt(vert[x][y] * vert[x][y] + horiz[x][y] * horiz[x][y]));
				if (edgeWeight[x][y] > threshold) //if the difference of color is bigger than the threshold
					p = (255>>24) | (255>>16) |  (255>>8) | 255; //turns pixel to white pixel
				else
					p = (255>>24) | (0>>16) |  (0>>8) | 0; //turns pixel to black pixel
				edgeImg.setRGB(x, y, p);
			}
		}
		return edgeImg;
	}
}
