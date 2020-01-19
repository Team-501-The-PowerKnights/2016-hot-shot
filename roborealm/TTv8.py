# -*- coding: utf-8 -*-
"""
Created on Sat Mar  5 16:54:45 2016

@author: Ithier
"""

# Targeting and Tracking for FIRST 2016 Game
# Carter Ithier
# carter.ithier@gmail.com

# This version calclulates distance, angle, number of targets, and the x and y location of the center of the rectangular target
# It calculates angle based on width AND height parameters 
# It determines an adjusted distance based on a 4th degree polynomial curve fitter (which needs to be updated to correct eq for new robot) 
# Lastly it is able to determine which target to go after if it sees more than one, tells you if you are aligned, and which direction to go to become more aligned
# Shooting is prioritized by a combination of what is most straight on and what is closest to center 

# This version selects external contours only if there are multiple BFRs and prioritizeTarget depends on distance  
# This version tests how to make things more accurate when camera is horizontal

import rr 
import math 

# initialize variables 
validUpdate = False  # boolean determining if data is good/if we should use data 
rr.SetVariable("validUpdate", validUpdate)
targetPixelHeight = 0
targetPixelWidth = 0
targetSamples = 0
distFromCenter = [] # the distance of the target's center from the camera's center 
lrDiff_list = [] # a list of leftyy - rightyy for each blob
area_list = [] # a list for the area of each blob 
thresholdC = .4 # threshold value for when considering an appropriate distance a target is from center when it is straight on  
maxDist = 20 # maximum distance we'll be shooting from (need to change to 21 eventually)
minDist = 8
closeness = 8 # (need to experiment with this number) The distance when close enough out threshold C number shouldn't matter
towerH = (89/12) # height from ground to center of target (in real life) in ft 
 

# camera variables (from camera documentation). They are swapped because the camera is horizontal on the robot
horizontal_cameraFOV = 36.5 # degrees   note: FOV = Field Of View 
vertical_cameraFOV = 47.0 # degrees 3
robot2camera = 1.5 # ft    distance from front of robot to camera (so that distance calculation is distance from tower to front of robot)


# variables from RoboRealm (in pixels)
imageHeight = rr.GetVariable("IMAGE_HEIGHT") 
imageWidth = rr.GetVariable("IMAGE_WIDTH") 
centerX = imageWidth / 2 # center of camera image
centerY = imageHeight / 2 
minY = 220 # y values below this in BFR are from blobs created by the bar 
		
# dimensions from target (from game manual)
targetHeight = 14.0 # in  
targetWidth = 20.0 # in 	


# Declare Functions 
def xIsValid(minX, maxX, lowerRightX, upperRightX, lowerLeftX, upperLeftX):
	if lowerRightX < maxX and upperRightX < maxX and lowerLeftX > minX and upperLeftX > minX :
		return True
	else:
		return False
			
def yIsValid(minY, maxY, lowerRightY, upperRightY, lowerLeftY, upperLeftY):
	if lowerRightY > minY and upperRightY < maxY and lowerLeftY > minY and upperLeftY < maxY :
		return True
	else:
		return False

def findDistance(targetActual, imagePx, targetPx, cameraFOV):
	totalDistance = (((targetActual*imagePx)/targetPx)/2) / \
			math.tan(((cameraFOV*math.pi)/180.0)/2.0)
	totalDistance = int((totalDistance*100)/12)/100 + robot2camera # make into 2 decminal pt and ft
	return totalDistance

def changeCoordinates2(x,y):  # coordinate values for variable display only. Based on idea that center of screen is (0,0) and max value is 1
	X_LocationD = (x - centerX) / centerX
	X_LocationD = int(X_LocationD*100)/100
	
	Y_LocationD = (y - centerY) / centerY
	Y_LocationD = int(Y_LocationD*100)/100
	return (X_LocationD, Y_LocationD)

def changeCoordinates1(x):  # coordinate values for variable display only. Based on idea that center of screen is (0,0) and max value is 1
	X_LocationD = (x - centerX) / centerX
	return (X_LocationD)

def fixDistance(x): # use 4th order polynomial fit from MATLAB to adjust for error and get more exact result
	#distance = -.0276851884*math.pow(x,3) + 1.02038482*math.pow(x,2) - 11.1448497*x + 43.5085374
	distance = (-2.9565*10**-6*x**3 + 0.0017*x**2 + 0.6935*x + 11.8683)/12 
	#distance = -2.19024798*10**-12*x**3 -1.86339832*10**-12*x**2 + x = 9.00217776*10**-12 
	distance = int(distance*100)/100
	return distance
		
def prioritizeTarget(distFromCenter, lrDiff_list, distance): # prioritize which target to use based on combo of whether it's straightOn and how close to center it is
	inRange = [i for i, x in enumerate(distFromCenter) if math.fabs(x) < thresholdC]
	if len(inRange) == 2 or distance < closeness:
		index = lrDiff_list.index(min(lrDiff_list)) 
	elif len(inRange) == 1:
		index = inRange[0]
	else: 
		index = distFromCenter.index(min(distFromCenter)) 
	
	index = index*8  # adjust for fact that each 8 corresponds to one blob when indexing rectangleCoordinates
	
	return index
	

def badBlob(BFR):
	needToRemove = False
	blob =[]
	final_remove = []
	
	# Calculate area for each BFR 
	for i in range(0, len(BFR), 8):
		rightx = BFR[i] # bottom right corner
		righty = BFR[i + 1]
		leftx = BFR[i + 2] # bottom left corner
		lefty = BFR[i + 3]
		leftxx = BFR[i + 4] # top left corner
		leftyy = BFR[i + 5]
		rightxx = BFR[i + 6] # top right corner
		rightyy = BFR[i + 7]
		
		if (righty < minY or lefty < minY or rightyy < minY or leftyy < minY):
			needToRemove = True
			blob.append(i)
			
	if (needToRemove):
		for i in range(0,len(blob)):
			remove = list(range(blob[i],blob[i]+8))
			final_remove = final_remove + remove 
			
		for i in range(len(final_remove)-1,-1, -1):
			BFR.pop(final_remove[i])
		
		BFR_fixed = BFR 
              
	
	else:
		BFR_fixed = BFR
		
	return BFR_fixed
			
	
			
	
			
def externalContour(BFR):
	# Calculate area for each BFR 
	for i in range(0, len(BFR), 8):
		rightx = BFR[i] # bottom right corner
		righty = BFR[i + 1]
		leftx = BFR[i + 2] # bottom left corner
		lefty = BFR[i + 3]
		leftxx = BFR[i + 4] # top left corner
		leftyy = BFR[i + 5]
		rightxx = BFR[i + 6] # top right corner
		rightyy = BFR[i + 7]
		
		width = (abs(leftxx - rightxx) + abs(leftx - rightx))/2
		height = (abs(leftyy - lefty) + abs(rightyy - righty))/2
		area = int(width*height*100)/100
		area_list.append(area)
	
	
	
	# Sort areas from lowest to highest and grab values and indices for two largest
	area_list_sorted = sorted(area_list)
	
	area1 = area_list_sorted[-1]
	area2 = area_list_sorted[-2]

	area1_index = area_list.index(area1)
	area2_index = area_list.index(area2)


	# Determine which blob(s) we should keep
	if area1 > 2*area2:
		# then area1 is only good blob and we want that one
		blob = [area1_index] 
	else:
		# both blobs are valid and we should keep both
		blob = [area1_index, area2_index]


	# Give correct rectangleCoordinates 
	if len(blob) == 1:
		rectangleCoordinates = BFR[blob[0]*8: blob[0]*8 + 8]
	else:
		rectangleCoordinates = BFR[blob[0]*8: blob[0]*8 + 8] + BFR[blob[1]*8: blob[1]*8 + 8]

	return rectangleCoordinates

		
		
		
		
		
		
# Obtain Rectangle Coordinates
BFR_original = rr.GetArrayVariable("BFR_COORDINATES")
# BFR_Coordinates are generated from Replace Blob module in which p1x, 
# p1y, p2x, p2y, p3x, p4x, p4y are the coordinates of the best fit 
# rectangle of the replaced blob. Note that this is an Array with
# 8 numbers per blob

BFR = badBlob(BFR_original)
rr.SetArrayVariable("BFR_postBADBLOB", BFR)
rr.SetVariable("lenBFRpost", len(BFR))


# If more than one blob (8 coordinates) then find wanted blobs
if (len(BFR) > 8):
	rectangleCoordinates = externalContour(BFR)
else:
	rectangleCoordinates = BFR 
	
rr.SetArrayVariable("rectCoor", rectangleCoordinates)
rr.SetVariable("lenRectCoor", len(rectangleCoordinates))

# Run loop for calculations 		
# Check that array was returned
if isinstance(rectangleCoordinates, list):
	# Check that length of array > 0 and sees no more than 2 targets (it's impossible to see more than that) and externalContour() should have fixed that
	if len(rectangleCoordinates) > 0 and len(rectangleCoordinates) <= 2*8:
	
		# Loop through coordinates of blobs (8 per blob) and store values
		for i in range(0, len(rectangleCoordinates), 8):
			rightx = rectangleCoordinates[i] # bottom right corner
			righty = rectangleCoordinates[i + 1]
			leftx = rectangleCoordinates[i + 2] # bottom left corner
			lefty = rectangleCoordinates[i + 3]
			leftxx = rectangleCoordinates[i + 4] # top left corner
			leftyy = rectangleCoordinates[i + 5]
			rightxx = rectangleCoordinates[i + 6] # top right corner
			rightyy = rectangleCoordinates[i + 7]

			# check that x and y coordinates are reasonable	
			if xIsValid(0, imageWidth, rightx, rightxx, leftx, leftxx) and yIsValid(0, imageHeight, righty, rightyy, lefty, leftyy):
				# Based on these two side lines get the center line height
				# Center line is used since we want to aim to center of target
				# This also removes any perspective distortion where the right or
				# left size may be a couple inches closer or further away from cameraFieldOfView
				targetPixelHeight = targetPixelHeight + ((lefty - leftyy) + (righty - rightyy)) / 2 # Summation of avgs of targetPixelHeight
				
				# Alternatively we can do the same thing as above but with width instead. You don't need both but this gives extra data to compare
				targetPixelWidth = targetPixelWidth + ((leftxx - rightxx) + (leftx - rightx)) / 2 # Summation of avgs of targetPixelWidth
				
				if len(rectangleCoordinates) > 8: # if more than one blob
					# Determine which target is closest to the center 
					x_location = (leftx+leftxx+rightx+rightxx)/4 # x value of center of target
					x_locationAdjusted = math.fabs(changeCoordinates1(x_location)) # change coordinates of x value so that coordinate system is 0,0 at center and values [-1,1]
					distFromCenter.append(x_locationAdjusted)
					
					# Determine which target(s) are straightOn
					difference =(math.fabs(leftyy - rightyy) + math.fabs(lefty - righty))/2 
					lrDiff_list.append(difference)
								
				targetSamples = targetSamples + 1
				
		if targetSamples > 0:
			# Take average of targetPixelHeight and Width
			targetPixelHeight = targetPixelHeight / targetSamples	
			targetPixelWidth = math.fabs(targetPixelWidth / targetSamples)
			rr.SetVariable("targetPixelHeight", targetPixelHeight)
			rr.SetVariable("targetPixelWidth", targetPixelWidth)
		
			# Determine distance in ft and angle in degrees using height measurements 
			# (Ideally totalDistance_H and W should be the same because they are measuring the same thing)
			if targetPixelHeight and targetPixelWidth > 0:
				totalDistance_H = findDistance(targetHeight, imageHeight, targetPixelHeight, vertical_cameraFOV)
				totalDistance_W = findDistance(targetWidth, imageWidth, targetPixelWidth, horizontal_cameraFOV)
				distance_avg = (totalDistance_H + totalDistance_W) / 2
                        
				distance_horizontal = math.sqrt(totalDistance_W**2 - towerH**2)
				
				distance_final = int(fixDistance(distance_horizontal*12)*100)/100 
				
			
				# Find center of target
				if len(rectangleCoordinates) > 8:
					index = prioritizeTarget(distFromCenter, lrDiff_list, totalDistance_W) #blob number of prioritizeTarget    Input for distance might need to change!!!!
				else:
					index = 0;
				
				rr.SetVariable("index", index)
				
				#x_location = (leftx+leftxx+rightx+rightxx)/4
				x_location = (rectangleCoordinates[index] + rectangleCoordinates[index + 2] + rectangleCoordinates[index + 4] + rectangleCoordinates[index + 6]) / 4
				
				#y_location = (lefty+leftyy+righty+rightyy)/4
				y_location = (rectangleCoordinates[index + 1] + rectangleCoordinates[index + 3] + rectangleCoordinates[index + 5] + rectangleCoordinates[index + 7]) / 4
			
				# Use x values to determine center of target
				offsetpx = centerX - x_location # pixels
				
				angle_W = horizontal_cameraFOV * (offsetpx / imageWidth) 
				# uses proportions to find angle offset
				angle_W = int(angle_W*100)/100 
				
				angle_H = vertical_cameraFOV * (offsetpx / imageHeight)
				# uses proportions to find angle offset
				angle_H = int(angle_H*100)/100 
				
				angle_avg = int(((angle_W + angle_H) / 2)*100)/100 
			
				# Change Coordinates so values are relative to center
				x_locationD = changeCoordinates2(x_location, y_location)[0]
				y_locationD = changeCoordinates2(x_location, y_location)[1]
				
				#if (totalDistance_H and totalDistance_W) < maxDist and (totalDistance_H and totalDistance_W) > minDist:
				#if (totalDistance_H and totalDistance_W) < maxDist:				# fix to whatever our adjusted distance is (not both)
				#if distance_final > minDist and distance_final < maxDist:
											
					# Set Variables
				rr.SetVariable("Distance_H", totalDistance_H)
				rr.SetVariable("Distance_W", totalDistance_W)
				rr.SetVariable("Distance_avg", distance_avg)
				rr.SetVariable("Horizontal distance", distance_horizontal)
				rr.SetVariable("Distance", distance_final)
				rr.SetVariable("X_Location", x_location)
				rr.SetVariable("Y_Location", y_location)
				rr.SetVariable("X_LocationD", x_locationD)
				rr.SetVariable("Y_LocationD", y_locationD)
				rr.SetVariable("Angle_H", angle_H)
				rr.SetVariable("Angle_W", angle_W)
				rr.SetVariable("Angle", angle_avg)
				rr.SetVariable("Offset Px", offsetpx)
				rr.SetVariable("Num_Targets", targetSamples)
				
				
					
						
				validUpdate = True 
				rr.SetVariable("validUpdate", validUpdate)
	else:
		validUpdate = False
		rr.SetVariable("validUpdate", validUpdate)
		rr.SetVariable("RectCoor len", len(rectangleCoordinates))
		
				
				
				

				
		
		