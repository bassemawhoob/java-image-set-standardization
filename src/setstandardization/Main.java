package setstandardization;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class Main {
	public static void main(String... args) {
		
		//INPUT FILES DIRECTORY
		File[] files = new File("/Users/Bassem/Documents/trainingdata/faces/")
				.listFiles();

		List<BufferedImage> images = load(files);
		BufferedImage average = average(images);
		BufferedImage std = standard_deviation(images, average);
		
		// EXTRA: PRINTS THE MEAN IMAGE AND THE STANDARD DEVIATION IMAGE OF THE SET
		try {
			// OUTPUT DIRECTORY FOR BOTH FILES
			String path = "/Users/Bassem/Downloads/";
			File outputfile = new File(path + "trial-average.jpg");
			ImageIO.write(average, "jpg", outputfile);
			File outputfile2 = new File(path + "trial-std.jpg");
			ImageIO.write(std, "jpg", outputfile2);
		} catch (IOException e) {
			
		}
		standardize(images, average, std);
	}

	public static void standardize(List<BufferedImage> images,
			BufferedImage average, BufferedImage std) {
		// OUTPUT DIRECTORY	OF STANDARDIZED IMAGES
		String path = "/Users/Bassem/Downloads/standardized_training/";
		int i = 0;
		for (BufferedImage originalImage : images) {
			try {

				int w = originalImage.getWidth();
				int h = originalImage.getHeight();
						
				BufferedImage standardized_image = new BufferedImage(w, h,
						BufferedImage.TYPE_BYTE_GRAY);

				WritableRaster raster = standardized_image.getRaster()
						.createCompatibleWritableRaster();
				
				double[][] new_img_arr = new double[h][w];

				for (int y = 0; y < h; ++y) {
					for (int x = 0; x < w; ++x) {
						double ori = (originalImage.getRaster().getSample(x,
								y, 0) - average.getRaster().getSample(x, y, 0));
						double standard_dev = std.getRaster().getSample(x, y, 0);
						double value = ori/standard_dev;
						new_img_arr[y][x] = value;
					}
				}
				
				double[][] normalized_image_data = normalize_data(new_img_arr);
				for (int y = 0; y < h; ++y) {
					for (int x = 0; x < w; ++x) {
						raster.setSample(x, y, 0, normalized_image_data[y][x]*255);
					}
				}

				standardized_image.setData(raster);

				File outputfile = new File(path + i + ".jpg");
				ImageIO.write(standardized_image, "jpg", outputfile);
				System.out.println("Image standardized successfully: "
						+ outputfile.getPath());
				i++;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static double[][] normalize_data(double[][] numbers){
		double[][] new_arr = new double[numbers.length][numbers[0].length];
        double maxValue = numbers[0][0];
        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] > maxValue) {
                    maxValue = numbers[j][i];
                }
            }
        }
        double minValue = numbers[0][0];
        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] < minValue ) {
                    minValue = numbers[j][i];
                }
            }
        }
        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                  new_arr[j][i] = (numbers[j][i]-minValue)/(maxValue-minValue);
            }
        }
        return new_arr;
	}
	

	public static BufferedImage array_to_bi(int[][] image) {
		// Assuming that all images have the same dimensions
		int h = image.length;
		int w = image[0].length;

		BufferedImage final_img = new BufferedImage(w, h,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster raster = final_img.getRaster()
				.createCompatibleWritableRaster();

		for (int y = 0; y < h; ++y)
			for (int x = 0; x < w; ++x) {
				raster.setSample(x, y, 0, image[y][x]);
			}

		final_img.setData(raster);
		return final_img;
	}

	public static List<BufferedImage> load(File[] files) {
		int i = 0;
		List<BufferedImage> images = new ArrayList<BufferedImage>();

		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());
				load(file.listFiles()); // Calls same method again.
			} else {
				try {
					String extension = "";
					int j = file.getName().lastIndexOf('.');
					if (j > 0) {
						extension = file.getName().substring(j + 1);
						if (extension.equals("pgm")) {
							BufferedImage originalImage = array_to_bi(PGMIO
									.read(file));
							images.add(originalImage);
						} else if (extension.equals("jpg")
								|| extension.equals("png")) {
							BufferedImage originalImage = ImageIO.read(file);
							images.add(originalImage);
						}
						i++;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return images;
	}

	public static BufferedImage average(List<BufferedImage> images) {

		int n = images.size();

		// Assuming that all images have the same dimensions
		int w = images.get(0).getWidth();
		int h = images.get(0).getHeight();

		BufferedImage average = new BufferedImage(w, h,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster raster = average.getRaster()
				.createCompatibleWritableRaster();

		for (int y = 0; y < h; ++y)
			for (int x = 0; x < w; ++x) {
				float sum = 0.0f;
				for (int i = 0; i < n; ++i)
					sum = sum + images.get(i).getRaster().getSample(x, y, 0);

				raster.setSample(x, y, 0, Math.round(sum / n));
			}
		average.setData(raster);
		return average;
	}

	public static BufferedImage standard_deviation(List<BufferedImage> images,
			BufferedImage mean) {

		int n = images.size();

		// Assuming that all images have the same dimensions
		int w = images.get(0).getWidth();
		int h = images.get(0).getHeight();

		BufferedImage standard_devation = new BufferedImage(w, h,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster raster = standard_devation.getRaster()
				.createCompatibleWritableRaster();

		for (int y = 0; y < h; ++y)
			for (int x = 0; x < w; ++x) {
				double sum = 0.0f;
				for (int i = 0; i < n; ++i)
					sum = sum
							+ Math.pow(
									images.get(i).getRaster()
											.getSample(x, y, 0)
											- mean.getRaster().getSample(x, y,
													0), 2);

				raster.setSample(x, y, 0, Math.round(Math.sqrt(sum / n)));
			}

		standard_devation.setData(raster);
		return standard_devation;
	}

	public static void printBufferedImage(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[][] arr = new int[h][w];

		for (int y = 0; y < h; ++y)
			for (int x = 0; x < w; ++x) {
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);
				arr[y][x] = (r + g + b) / 3;
			}

		 for (int[] row : arr){
			 System.out.println(Arrays.toString(row));
		 }
	}
}
