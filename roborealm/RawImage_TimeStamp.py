import rr 
import time


t= time.localtime()

month = t[1]
day = t[2]
year = t[0]

hour = t[3]
min = t[4]
sec = t[5]

stamp = str(month) + "_" + str(day) + "_" + str(year) + "_time_" + str(hour) + "_" + str(min) + "_" + str(sec)

name = "C://F_FIRST//RoboRealm//Raw Images From Match//" + stamp + ".jpg"

rr.SetVariable("raw_filename", name)