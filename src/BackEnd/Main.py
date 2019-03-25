import BackEnd.JakesFunctions
import BackEnd.OurClasses
import time
# import sqlite3

breakLoop = False
currentTime = 0.0

# code in this block will execute every 0.1 seconds (not including runtime)
while True:

    if breakLoop:
        break

    time.sleep(0.1)
    currentTime += 0.1
