FILES

Main.java - executable function*

CPU.java - Has all CPU Logic*

Memory.java - Has all memory Logic*

*More detail on implementaion in Summary.docx

Sample5.txt

Sample5 is my custom program, it generates a random number between 1-100 using the Get instruction,
and figures out if it is even or odd.

Since there is no comparison operations in the Instruction Set, it will keep decrementing by 2 in a loop,

The loops condition is Jump if not equal to zero, if it ever equals zero it jumps to code that prints
the number and states that it is even and exits.

The timer interupts checks for odd number, if it times out it states that number is odd and exits

*IMPORTANT* to allow for program to decrement to zero from 100, the timer must be at least 124!!

I repreat the timer must be at least 124 for program to work properly.


HOW TO EXECUTE:

javac *.java (if u need to compile)

java Main sample1.txt 30
java Main sample2.txt 30
java Main sample3.txt 30
java Main sample4.txt 30
java Main sample5.txt 124

This project was coded in VS Code using Java 16.0.2 for Professor Ozbirn, on March 1st 2023.