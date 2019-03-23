# Image dataset standardization
A Java program to standardize a set of images to have a mean 0 and standard deviation 1.
The program works by subtracting the dataset mean to "center" the data. 
Additionally, divide by the standard deviation of that feature or pixel as well to normalize each feature value to a z-score. This step is crucial in training any neural network

To learn more about image data pre-processing for Neural Networks please visit this [page](https://becominghuman.ai/image-data-pre-processing-for-neural-networks-498289068258)  
**Note:** all the images in your dataset **must** be of equal sizes (250x250 for example)
## How to run

1. Place all your images in one folder (images must be in .png of .jpg format)
2. Edit Main.java with your input and output directories 
3. Run Main.java

The program will output the every image in the set standarized, the mean image and the standard deviation image of the set named (trial-average.jpg and trial-std.jpg, respectively)

