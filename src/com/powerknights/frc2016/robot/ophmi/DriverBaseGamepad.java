/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.ophmi;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.config.hw.GamepadModels;
import com.powerknights.frc2016.robot.config.hw.LogitechDualActionGamepad;
import com.powerknights.frc2016.robot.config.hw.LogitechF301Gamepad;
import com.powerknights.frc2016.robot.config.hw.Xbox360Gamepad;

import edu.wpi.first.wpilibj.Joystick;


/**
 * @author first.stu
 **/
public abstract class DriverBaseGamepad
   extends Gamepad
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( DriverBaseGamepad.class.getName() );

   private final GamepadModels model;


   protected DriverBaseGamepad( Joystick joystick, GamepadModels model )
   {
      super( joystick );

      this.model = model;
   }


   public int getSpeedAxis()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getLeftStickY();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getLeftStickY();
      case LogitechF310:
         return LogitechF301Gamepad.getLeftStickY();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getLeftStickY();
      }
   }


   public int getTurnAxis()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getRightStickX();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getRightStickX();
      case LogitechF310:
         return LogitechF301Gamepad.getRightStickX();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getRightStickX();
      }
   }


   public int getPitWinchEnableButton()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getStartButton();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getStartButton();
      case LogitechF310:
         return LogitechF301Gamepad.getStartButton();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getStartButton();
      }
   }


   public int getPitWinchInAxis()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getLeftTrigger();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getLeftTrigger();
      case LogitechF310:
         return LogitechF301Gamepad.getLeftTrigger();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getLeftTrigger();
      }
   }


   public int getPitWinchOutAxis()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getRightTrigger();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getRightTrigger();
      case LogitechF310:
         return LogitechF301Gamepad.getRightTrigger();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getRightTrigger();
      }
   }

}
