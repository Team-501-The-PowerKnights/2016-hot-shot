/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.config;


import com.powerknights.frc2016.robot.config.hw.RoboRIO;


/**
 * @author first.stu
 **/
public class LifterConfig
{

   /*
    * Scissor Lift (Motors)
    */

   /**
    * @return scissor lift speed controller / motor channel
    **/
   static public int getScissorLiftMotorChannel()
   {
      // TODO - Get CANID into RoboRIO config
      return 25;
   }


   /*
    * Winch (Motors)
    */

   /**
    * @return left winch speed controller / motor CAN ID
    **/
   static public int getLeftWinchMotorId()
   {
      // TODO - Get CANID into RoboRIO config
      return 23;
   }


   /**
    * @return right winch speed controller / motor CAN ID
    **/
   static public int getRightWinchMotorId()
   {
      // TODO - Get CANID into RoboRIO config
      return 24;
   }


   /*
    * Winch Aux (Motors)
    */

   /**
    * @return left winch aux speed controller / motor CAN ID
    **/
   static public int getLeftWinchAuxMotorId()
   {
      // TODO - Get CANID into RoboRIO config
      return 26;
   }


   /**
    * @return right winch aux speed controller / motor CAN ID
    **/
   static public int getRightWinchAuxMotorId()
   {
      // TODO - Get CANID into RoboRIO config
      return 27;
   }


   /*
    * Winch (Lock)
    */

   /**
    * @return winch lock servo channel
    **/
   static public int getWinchLockServoId()
   {
      return RoboRIO.getPwmChannel9();
   }

}
