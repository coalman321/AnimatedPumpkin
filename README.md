# AnimatedPumpkin
An animated Halloween Pumpkin on a book.
The java directory contains the Java code using open CV to do face recognition on a IP camera.
It sends the data via a UDP server to the PI's address.
The python directory contains the Python script running on the raspberry pi.
The PI recives the data sent to it (angle and acivation state) and drives the IO accordingly
(neck servo and eye lights and audio via MPG123).  



To run the system you must
--- on the PI ---
1. Install the rpi_ws281x into the python environment.
2. Install wiringpi and add it to the python environment.
3. Install / update mpg123 for audio.
4. Make sure the analog audio is not default or currently enabled (it will disable PWM to the servo).
5. Start the python script. The eyes should wipe blue and wait for communication from the server host to begin.
--- on the server ---
6. Install opencv-3.4.3 on the server computer.
7. Start the Vision application on the server computer. It should have the correct IP for the camera and PI compiled in.
8. The eyes should turn black and the console will declare "beginning animation".
9. It works.
