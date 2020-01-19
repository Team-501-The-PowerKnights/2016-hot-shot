/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.config;


/**
 * @author first.stu
 **/
public class ShooterConfig
{

   /*
    * Ball Feeder / Front Manipulator (Motors)
    */

   /**
    * @return front ball feeder speed controller / motor channel
    **/
   static public int getBallFeederChannel()
   {
      // TODO - Get CANID into RoboRIO config
      return 22;
   }


   /*
    * Shooter Wheels (Motors)
    */

   /**
    * @return left speed controller / motor channel
    **/
   static public int getLeftMotorChannel()
   {
      // TODO - Get CANID into RoboRIO config
      return 20;
   }


   /**
    * @return right speed controller / motor channel
    **/
   static public int getRightMotorChannel()
   {
      // TODO - Get CANID into RoboRIO config
      return 21;
   }


   /*
    * Shooter Elevation Angle (Motors)
    */

   /**
    * @return angle speed controller / motor channel
    **/
   static public int getElevationChannel()
   {
      // TODO - Get CANID into RoboRIO config
      return 28;
   }

}
