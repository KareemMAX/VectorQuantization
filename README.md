# VectorQuantization
Vector quantization compression and decompression program for gray-scale images. 
It was an assignment for the Compression and Information theory course in Cairo University Faculty of Computers and Artificial Intelligence.

## How to use:
To compress an image you can run the program with the following arguments:
```
java -jar vectorquantization.jar -c [filename] [vector size] [code book bit count]
```

For decompression, you can use:
```
java -jar vectorquantization.jar -d [filename]
```

## Examples:
Using `2x2` vector size and 16 code book size (4 bits)

### Input:
![Sample 1 input](examples/sample1.jpg)  
Size: 58.9 KB

### Output:
![Sample 1 output](examples/sample1.jpg.vq.png)  
[Compressed file](examples/sample1.jpg.vq) size: 19.6 KB

### Input:
![Sample 2 input](examples/sample2.jpg)  
Size: 46.3 KB

### Output:
![Sample 2 output](examples/sample2.jpg.vq.png)  
[Compressed file](examples/sample2.jpg.vq) size: 19.6 KB