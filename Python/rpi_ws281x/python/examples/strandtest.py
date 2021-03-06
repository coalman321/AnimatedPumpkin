import time
import socket
from neopixel import *
import wiringpi
import argparse

# LED strip configuration:
LED_COUNT      = 14      # Number of LED pixels.
LED_STRIP      = ws.SK6812_STRIP_GRBW
LED_PIN        = 10      # GPIO pin connected to the pixels (10 uses SPI /dev/spidev0.0).
LED_DMA        = 10      # DMA channel to use for generating signal (try 10 & DO NOT USE 5)
LED_BRIGHTNESS = 10      # Set to 0 for darkest and 255 for brightest
LED_CHANNEL    = 0       # set to '1' for GPIOs 13, 19, 41, 45 or 53

SERVO_PIN      = 18      # GPIO pin connected to the servo (18 uses PWM!)

UDP_PORT = 1110

TEST_MODE = False;

# Define functions which animate LEDs in various ways.
def colorWipe(strip, color, wait_ms=50):
    """Wipe color across display a pixel at a time."""
    for i in range(strip.numPixels()):
        strip.setPixelColor(i, color)
        strip.show()
        time.sleep(wait_ms/1000.0)
        
def hwDebug():
            wiringpi.pwmWrite(18,101) #default servo to center
            colorWipe(strip, Color(255, 0, 0))  # Red wipe
            colorWipe(strip, Color(0, 255, 0))  # Blue wipe
            colorWipe(strip, Color(0, 0, 255))  # Green wipe
            wiringpi.pwmWrite(18,200) #default servo to center
            colorWipe(strip, Color(255, 0, 0))  # Red wipe
            colorWipe(strip, Color(0, 255, 0))  # Blue wipe
            colorWipe(strip, Color(0, 0, 255))  # Green wipe

# Main program logic follows:
if __name__ == '__main__':

    if TEST_MODE:
        print ('hardware debug mode')
    else:
        print ('Beginning animation')
    
    #setup servo GPIO
    wiringpi.wiringPiSetupGpio()
    wiringpi.pinMode(SERVO_PIN, wiringpi.GPIO.PWM_OUTPUT)
    wiringpi.pwmSetMode(wiringpi.GPIO.PWM_MODE_MS)
    wiringpi.pwmSetClock(192)
    wiringpi.pwmSetRange(2000)
    wiringpi.pwmWrite(SERVO_PIN,150) #default servo to center

    # Create NeoPixel object with appropriate configuration.
    strip = Adafruit_NeoPixel(LED_COUNT, LED_PIN, 800000, LED_DMA, False, LED_BRIGHTNESS, LED_CHANNEL, LED_STRIP)
    strip.begin()# Intialize the library (must be called once before other functions).
    
    #create UDP socket at local address
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind(('',UDP_PORT))
    
    colorWipe(strip, Color(255, 0, 0), 0)  # Red wipe eyes
    
    wakestate = 0
    prevwake = 0
    turn = 50
    
    try:
        while True:
            if TEST_MODE:
                hwDebug()
            else:
                try:
                    data, addr = sock.recvfrom(1024) #buffer size 1024
                    turn = int(str(data)[:2])
                    wakestate = int(str(data)[4:])
                    if wakestate == 1 and prevwake == 0:
                        wiringpi.pwmWrite(18,100 + int(turn)) #default servo to center
                        colorWipe(strip, Color(255, 0, 0), 0)  # Red wipe eyes
                        os.system('mpg ')
                        prevwake = 1
                    elif wakestate == 1 and prevwake == 1:
                        wiringpi.pwmWrite(18,100 + turn) #default servo to center
                    else:
                        colorWipe(strip, Color(0,0,0), 0)
                        prevwake = 0
                except ValueError:
                    print "whoops"        
    except KeyboardInterrupt:
        colorWipe(strip, Color(0,0,0), 0)
        wiringpi.pwmWrite(18,150) #default servo to center
