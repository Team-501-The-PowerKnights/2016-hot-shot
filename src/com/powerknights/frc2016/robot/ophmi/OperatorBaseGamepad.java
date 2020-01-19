/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
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
public abstract class OperatorBaseGamepad
   extends Gamepad
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( OperatorBaseGamepad.class.getName() );

   private final GamepadModels model;


   protected OperatorBaseGamepad( Joystick joystick, GamepadModels model )
   {
      super( joystick );

      this.model = model;
   }


   public int getShooterElevationAxis()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getRightStickY();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getRightStickY();
      case LogitechF310:
         return LogitechF301Gamepad.getRightStickY();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getRightStickY();
      }
   }


   public int getKickOutButton()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getGreenButton();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getGreenButton();
      case LogitechF310:
         return LogitechF301Gamepad.getGreenButton();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getGreenButton();
      }
   }


   public int getFireButton()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getRedButton();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getRedButton();
      case LogitechF310:
         return LogitechF301Gamepad.getRedButton();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getRedButton();
      }
   }


   public int getFeederPositionPOVIndex()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getPOV0();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getPOV0();
      case LogitechF310:
         return LogitechF301Gamepad.getPOV0();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getPOV0();
      }
   }


   public int getFeederSpeedAxis()
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


   public int getShotSpeedAxis()
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
         return LogitechF301Gamepad.getRightStickY();
      }
   }


   public int getLatchButton()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getLeftBumper();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getLeftBumper();
      case LogitechF310:
         return LogitechF301Gamepad.getLeftBumper();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getLeftBumper();
      }
   }


   public int getAutoElevationButton()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getYellowButton();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getYellowButton();
      case LogitechF310:
         return LogitechF301Gamepad.getYellowButton();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getYellowButton();
      }
   }


   public int getLiftButton()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getRightBumper();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getRightBumper();
      case LogitechF310:
         return LogitechF301Gamepad.getRightBumper();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getRightBumper();
      }
   }


   /**
    * Returns index to be used for the lift enable button. This comes from the
    * <b>Driver</b> gamepad, and is used to provide two person control of the
    * final lifting function.
    *
    * @return
    **/
   public int getLiftEnableButton1()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getLeftBumper();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getLeftBumper();
      case LogitechF310:
         return LogitechF301Gamepad.getLeftBumper();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getLeftBumper();
      }
   }


   /**
    * Returns index to be used for the lift enable button. This comes from the
    * <b>Driver</b> gamepad, and is used to provide two person control of the
    * final lifting function.
    *
    * @return
    **/
   public int getLiftEnableButton2()
   {
      switch ( model )
      {
      case Xbox360:
         return Xbox360Gamepad.getRightBumper();
      case LogitechDualAction:
         return LogitechDualActionGamepad.getRightBumper();
      case LogitechF310:
         return LogitechF301Gamepad.getRightBumper();
      default:
         logger.warn( "default gamepad used as none specified" );
         return LogitechF301Gamepad.getRightBumper();
      }
   }

}
