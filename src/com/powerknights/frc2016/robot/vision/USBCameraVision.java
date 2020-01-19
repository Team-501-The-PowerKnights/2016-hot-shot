/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.vision;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;

import edu.wpi.first.wpilibj.CameraServer;


/**
 * @author first.adam
 * @author first.stu
 **/
public class USBCameraVision
{

   /** Our classes' logger **/
   @SuppressWarnings( "unused" )
   private static final Logger logger =
      RioLogger.getLogger( USBCameraVision.class.getName() );

   /** Handle to the camera we are wrapping **/
   private final CameraServer server;
   /** Name of the camera / device used to stream data from **/
   private final String deviceName;


   public USBCameraVision( String deviceName )
   {
      // Don't need this until we start; but gather through constructor
      this.deviceName = deviceName;

      server = CameraServer.getInstance();

      server.setQuality( 75 );
      server.setSize( 1 ); // 320 x 240 (kSize320x240)
   }


   public void start()
   {
      server.startAutomaticCapture( deviceName );
   }

}
