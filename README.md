# AnimatedPumpkin
An animated Halloween Pumpkin on a book.
The java directory contains the Java code using open CV to do face recognition on a IP camera.
It sends the data via a UDP server to the PI's address.
The python directory contains the Python script running on the raspberry pi.
The PI recives the data sent to it (angle and acivation state) and drives the IO accordingly
(neck servo and eye lights and audio via MPG123).  
