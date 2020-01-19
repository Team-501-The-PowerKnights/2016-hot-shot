/**
 * Copyright (c) Team 501 Power Knights 2015, 2016. All Rights Reserved. Open
 * Source Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
 **/

package com.powerknights.frc2016.robot.ophmi;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.Level;
import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.config.hw.GamepadModels;
import com.powerknights.frc2016.robot.managers.PreferencesManager;
import com.powerknights.frc2016.robot.modules.WinchModule;
import com.powerknights.frc2016.robot.subsystems.Chassis;
import com.powerknights.frc2016.robot.subsystems.DriveTrain;
import com.powerknights.frc2016.utils.StringUtils;

import edu.wpi.first.wpilibj.Joystick;


/**
 * @author first.stu
 **/
public class DriverGamepad
   extends DriverBaseGamepad
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( DriverGamepad.class.getName() );


   static
   {
      // LOGGER - Override of default level
      RioLogger.setLevel( logger, Level.DEBUG );
   }

   /** Handle to the robot drive **/
   private final DriveTrain drive;
   /** Handle to the robot chassis **/
   @SuppressWarnings( "unused" )
   private final Chassis chassis;

   /** Whether to drive Stu's or WPI's way **/
   private final boolean driveStusWay;

   /** 'Index' of the speed input device axis **/
   private final int speedAxis;
   /** 'Index' of the turn input device axis **/
   private final int turnAxis;
   /** **/
   private boolean flattenInputs; // comes from I/O on laptop

   /** 'Index' of the winch in speed input device axis (pit) **/
   private final int winchInAxis;
   /** 'Index' of the winch out speed input device axis (pit) **/
   private final int winchOutAxis;
   /** 'Index' of the winch enable button (pit) **/
   private final int winchEnableButton;
   /** **/
   private boolean isPitWinchEnabled;


   public DriverGamepad( Joystick joystick, GamepadModels model )
   {
      super( joystick, model );
      logger.info( "constructing for " + model );

      drive = DriveTrain.getInstance();
      chassis = Chassis.getInstance();

      if ( PreferencesManager.getInstance().useStuDriveMode() )
      {
         smartDashboard.putString( "driveMode", "stusArcadeWay" );
         driveStusWay = true;
      }
      else
      {
         smartDashboard.putString( "driveMode", "wpisArcadeWay" );
         driveStusWay = false;
      }

      speedAxis = getSpeedAxis();
      turnAxis = getTurnAxis();

      winchInAxis = getPitWinchInAxis();
      winchOutAxis = getPitWinchOutAxis();
      winchEnableButton = getPitWinchEnableButton();
      isPitWinchEnabled = false;

      logger.info( "constructed" );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2015.ophmi.IHmiController#initForModeStart()
    */
   @Override
   public void initForModeStart()
   {
      initState();

      flattenInputs =
         PreferencesManager.getInstance().doFlattenDriverInputResponse();
   }


   private void initState()
   {
   }


   /*
    * (non-Javadoc)
    *
    * @see
    * com.powerknights.frc2012.modes.teleoperated.IDriverControl#performUpdate
    * ()
    */
   @Override
   public void performUpdate()
   {
      // Do the speed setting based on joystick positions
      processDriving();

      // For the pit only
      // TODO - Find a way to make this only work in the pit
      processPitWinching();
   }


   private void processDriving()
   {
      final double speed = getSpeed();
      smartDashboard.putNumber( "speed", speed );
      final double turn = getTurn();
      smartDashboard.putNumber( "turn", turn );

      driveArcadeWay( speed, turn );
   }


   private void driveArcadeWay( double speed, double turn )
   {
      final boolean zeroSpeed = isZero( speed );
      final boolean zeroTurn = isZero( turn );

      if ( zeroSpeed && zeroTurn )
      {
         drive.stop();
      }
      else
      {
         if ( driveStusWay )
         {
            drive.arcadeDrive( speed, turn );
         }
         else
         {
            drive.arcadeDriveWPI( speed, turn );
         }
      }
   }


   private double getSpeed()
   {
      final double speed = joystick.getRawAxis( speedAxis );
      smartDashboard.putNumber( "hmiSpeed", speed );
      if ( isZero( speed ) )
      {
         return 0.0;
      }
      else
      {
         return adjustSpeedInputs( speed );
      }
   }


   private double adjustSpeedInputs( double speed )
   {
      // TWEAK - Flattening response curve of joysticks
      if ( flattenInputs )
      {
         speed = InputTweaker.exponentiate( speed );
         speed = InputTweaker.square( speed );
      }

      return adjustSpeedSign( speed );
   }


   /**
    * Ensure the sign of the speed is correct for robot convention; namely
    * "forward" is positive, and "backward" is negative.
    *
    * @param speed
    * @return
    **/
   private double adjustSpeedSign( double speed )
   {
      return -speed;
   }


   private double getTurn()
   {
      final double turn = joystick.getRawAxis( turnAxis );
      smartDashboard.putNumber( "hmiTurn", turn );
      if ( isZero( turn ) )
      {
         return 0.0;
      }
      else
      {
         return adjustTurn( turn );
      }
   }


   private double adjustTurn( double turn )
   {
      // TWEAK - Flattening response curve of thumb sticks
      if ( flattenInputs )
      {
         turn = InputTweaker.exponentiate( turn );
         turn = InputTweaker.square( turn );
      }

      return adjustTurnSign( turn );
   }


   /**
    * Ensure the sign of the turn is correct for robot convention; namely "left"
    * is negative, and "right" is positive.
    *
    * @param speed
    * @return
    **/
   private double adjustTurnSign( double turn )
   {
      return -turn;
   }


   /**
    *
    **/
   private void processPitWinching()
   {
      final boolean isWinchEnabled = getWinchEnableButtonPressed();
      if ( !isPitWinchEnabled && isWinchEnabled )
      {
         isPitWinchEnabled = true;
      }
      else if ( isPitWinchEnabled && !isWinchEnabled )
      {
         isPitWinchEnabled = false;
      }
      if ( isPitWinchEnabled )
      {
         final double winchInSpeed = getWinchInSpeed();
         final double winchOutSpeed = getWinchOutSpeed();
         logger.debug( "PitWinch: in={}, out={}",
            StringUtils.format( winchInSpeed, 2 ),
            StringUtils.format( winchOutSpeed, 2 ) );
         // TODO - Put in the actual call to winch movement
         if ( winchOutSpeed > 0.0 )
         {
            // logger.trace( "winch 'out' with speed = {}", winchOutSpeed );
            WinchModule.getInstance().out( winchOutSpeed );
         }
         else if ( winchInSpeed > 0.0 )
         {
            // logger.trace( "winch 'out' with speed = {}", winchInSpeed );
            WinchModule.getInstance().in( winchInSpeed );
         }
         else
         {
            logger.trace( "winch stop" );
            WinchModule.getInstance().stop();
         }
      }
   }


   private boolean getWinchEnableButtonPressed()
   {
      final boolean pressed = joystick.getRawButton( winchEnableButton );
      return pressed;
   }


   private double getWinchInSpeed()
   {
      final double speed = joystick.getRawAxis( winchInAxis );
      if ( isZero( speed ) )
      {
         return 0.0;
      }
      else
      {
         return adjustWinchInSpeedInputs( speed );
      }
   }


   private double adjustWinchInSpeedInputs( double speed )
   {
      // TWEAK - Flattening response curve of joysticks
      if ( flattenInputs )
      {
         speed = InputTweaker.exponentiate( speed );
         speed = InputTweaker.square( speed );
      }
      speed *= 0.15;

      return adjustWinchInSpeedSign( speed );
   }


   /**
    * Ensure the sign of the speed is correct for robot convention; namely
    * "forward" is positive, and "backward" is negative.
    *
    * @param speed
    * @return
    **/
   private double adjustWinchInSpeedSign( double speed )
   {
      return speed;
   }


   private double getWinchOutSpeed()
   {
      final double speed = joystick.getRawAxis( winchOutAxis );
      if ( isZero( speed ) )
      {
         return 0.0;
      }
      else
      {
         return adjustWinchOutSpeedInputs( speed );
      }
   }


   private double adjustWinchOutSpeedInputs( double speed )
   {
      // TWEAK - Flattening response curve of joysticks
      if ( flattenInputs )
      {
         speed = InputTweaker.exponentiate( speed );
         speed = InputTweaker.square( speed );
      }
      speed *= 0.15;

      return adjustWinchOutSpeedSign( speed );
   }


   /**
    * Ensure the sign of the speed is correct for robot convention; namely
    * "forward" is positive, and "backward" is negative.
    *
    * @param speed
    * @return
    **/
   private double adjustWinchOutSpeedSign( double speed )
   {
      return speed;
   }

}
