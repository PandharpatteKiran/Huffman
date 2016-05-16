This implementation works not only on text fles (e.g.  .txt, .c, .java etc) but also on binary files (e.g. .pdf,.jpg, .mpeg, and other executables). 
Argument filename in the command-line below is an arbitrary file-name. It can be the name of any file, or text source or image or movie or something else. 
The suffix (sometimes called the extension) of filename is what follows the dot (sometimes we include the dot as part of the suffix). 
Huffman coding (encoding or decoding) of a 2MB file does not take more than 15-30 seconds in Java, C, or C++ on an machine with reasonable load.

// Encode : converts filename      ---> filename.huf   ; eg x.pdf into x.pdf.huf
//                                      in the process x.pdf gets erased and x.pdf.huf gets created
// Decode : converts filename.huf  ---> filename       ; eg x.pdf.huf into x.pdf
//                                      in the process x.pdf.huf get erased and x.pdf gets overwritten if
//                                      it already exists!
