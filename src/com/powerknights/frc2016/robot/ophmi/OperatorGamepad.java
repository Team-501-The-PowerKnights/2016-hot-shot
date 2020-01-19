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
import com.powerknights.frc2016.robot.modules.FeederPosition;
import com.powerknights.frc2016.robot.subsystems.Chassis;
import com.powerknights.frc2016.robot.subsystems.DriveTrain;
import com.powerknights.frc2016.robot.subsystems.Lifter;
import com.powerknights.frc2016.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;


/**
 * @author first.sam
 **/
public class OperatorGamepad
   extends OperatorBaseGamepad
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( OperatorGamepad.class.getName() );


   static
   {
      // LOGGER - Override of default level
      // RioLogger.setLevel( logger, Level.DEBUG );
   }

   /** Handle to the robot drive **/
   private final DriveTrain drive;
   /** Handle to the robot chassis **/
   private final Chassis chassis;
   /** Handle to the shooter subsystem **/
   private final Shooter shooter;
   /** Handle to the lifter subsystem **/
   private final Lifter lifter;

   /** Operator control button IDs **/
   private final int kickOutButton;
   private final int fireButton;
   private final int autoElevationButton;
   private final int latchButton;
   private final int liftButton;

   /** Operator control axis IDs **/
   private final int shooterElevationAxis;
   private final int shotSpeedAxis;
   private final int feederSpeedAxis;

   /** Operator control POV IDs **/
   private final int feederPositionPOVIndex;

   /** Speed to be used for shooter angle **/
   private double elevationSpeed;
   /** Flag to keep state on auto-elevation process **/
   private boolean autoElevationActive;
   /** Flag to debounce button **/
   private boolean lastAutoElevationButton;

   /** Shoot timer for automatic control **/
   private final Timer shootTimer;
   /** Speed to be used for shooter **/
   private static final double defaultShotSpeed = 1.00;

   /** Speed to be used for feeder **/
   private double feederSpeed;
   /** Position to be used for feeder **/
   private FeederPosition feederPosition;
   /** Used to 'debounce' feeder position **/
   private FeederPosition lastFeederPosition;
   /** Flag to keep state on feeder position process **/
   private boolean feederPositionActive;

   /** Hang timer for automatic control **/
   private final Timer hangTimer;
   /** Flag to keep state on hanging process **/
   private boolean doingHanging;
   /** Telemetry state name for hanging **/
   private final String doingHangingTelemetryState = "doingHanging";

   /** Handle to Driver's joystick for two-person control **/
   private final Joystick driverJoystick;
   /** Driver control button IDs **/
   private final int liftEnableButton1;
   /** Driver control button IDs **/
   private final int liftEnableButton2;
   /** Flag to keep state on lift enabling process **/
   private boolean liftEnabled;
   /** Telemetry state name for hanging **/
   private final String liftEnabledTelemetryState = "liftEnabled";


   public OperatorGamepad( Joystick joystick, GamepadModels model,
      Joystick driverJoystick )
   {
      super( joystick, model );
      logger.info( "constructing for " + model );

      drive = DriveTrain.getInstance();
      chassis = Chassis.getInstance();
      shooter = Shooter.getInstance();
      lifter = Lifter.getInstance();

      shooterElevationAxis = getShooterElevationAxis();

      kickOutButton = getKickOutButton();
      fireButton = getFireButton();
      autoElevationButton = getAutoElevationButton();

      shotSpeedAxis = getShotSpeedAxis();
      feederSpeedAxis = getFeederSpeedAxis();
      feederPositionPOVIndex = getFeederPositionPOVIndex();

      shootTimer = new Timer();

      latchButton = getLatchButton();
      liftButton = getLiftButton();

      hangTimer = new Timer();

      this.driverJoystick = driverJoystick;
      liftEnableButton1 = getLiftEnableButton1();
      liftEnableButton2 = getLiftEnableButton2();

      logger.info( "constructed" );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2013.ophmi.IHmiController#initForModeStart()
    */
   @Override
   public void initForModeStart()
   {
      initState();
   }


   /***********************************
    * State Management Section
    ***********************************/

   private void initState()
   {
      shootTimer.stop();
      shootTimer.reset();

      autoElevationActive = false;
      lastAutoElevationButton = false;

      feederPositionActive = false;
      lastFeederPosition = FeederPosition.UNSET;

      hangTimer.stop();
      hangTimer.reset();
      doingHanging = false;
      setDoingHanging( doingHanging );

      liftEnabled = false;
      setLiftEnabled( liftEnabled );
   }


   private void setDoingHanging( boolean hanging )
   {
      smartDashboard.putBoolean( doingHangingTelemetryState, hanging );
   }


   private void setLiftEnabled( boolean enabled )
   {
      smartDashboard.putBoolean( liftEnabledTelemetryState, enabled );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2013.ophmi.IHmiController#performUpdate()
    */
   @Override
   public void performUpdate()
   {
      // TODO - Don't need to process anything but hanging when hanging
      processShooterElevation();

      processShooting();

      processHanging();
   }

   private boolean loggedElevationMode = false;


   /**
    * Implements the shooter elevation functionality.
    **/
   private void processShooterElevation()
   {
      // First handle button press to enable / disable
      if ( isAutoElevationButtonPressed() && !lastAutoElevationButton )
      {
         lastAutoElevationButton = true;

         if ( !autoElevationActive )
         {
            autoElevationActive = true;
            shooter.enableAutoElevation();
         }
         else
         {
            autoElevationActive = false;
            shooter.homeElevation();
            // FIXME - Put delay in module; not here to wait for home
            Timer.delay( 2.0 );
            shooter.disableAutoElevation();
         }
      }
      else if ( !isAutoElevationButtonPressed() )
      {
         lastAutoElevationButton = false;
      }

      if ( autoElevationActive )
      {
         if ( !loggedElevationMode )
         {
            logger.debug( "run elevation position" );
            loggedElevationMode = true;
         }
         final double distance = chassis.getDistanceToTower();
         logger.trace( "distanceToTower={}", distance );
         if ( distance > 0.0 )
         {
            shooter.setForDistance( distance );
         }
      }
      else
      {
         if ( loggedElevationMode )
         {
            logger.debug( "run elevation speed" );
            loggedElevationMode = false;
         }
         elevationSpeed = getShooterElevationSpeed();
         shooter.adjustElevation( elevationSpeed );
      }
   }


   private boolean isAutoElevationButtonPressed()
   {
      final boolean pressed = joystick.getRawButton( autoElevationButton );
      return pressed;
   }


   private double getShooterElevationSpeed()
   {
      final double speed = joystick.getRawAxis( shooterElevationAxis );
      smartDashboard.putNumber( "hmiElevationSpeed", speed );
      if ( isZero( speed ) )
      {
         return 0.0;
      }
      else
      {
         return adjustShooterElevationInputs( speed );
      }
   }


   private double adjustShooterElevationInputs( double speed )
   {
      // No flattening of response curve for this

      // No turbo for this

      // Scale by factor of two to prevent fast motion
      speed /= 3.0;

      return adjustShooterElevationSign( speed );
   }


   /**
    * Ensure the sign of the speed is correct for robot convention; namely
    * "up"/"raise" is positive, and "down"/"lower" is negative.
    *
    * @param speed
    * @return
    **/
   private double adjustShooterElevationSign( double speed )
   {
      return -speed;
   }


   /**
    * Implements the shooting functionality.
    **/
   private void processShooting()
   {
      final boolean isFire = isFireButtonPressed();
      final boolean isKickOut = isKickOutButtonPressed();
      logger.trace( "autoShoot: isFire = {}, isKickOut = {}", isFire,
         isKickOut );

      if ( isFire && !isKickOut )
      {
         // Starting shot process
         if ( shootTimer.get() == 0.0 )
         {
            shootTimer.start();
            shooter.shoot( defaultShotSpeed );
         }
         // Done waiting for shooter prepare boulder position
         else if ( shootTimer.get() > 1.25 )
         {
            if ( feederPositionActive )
            {
               logger.debug( "toggling to disable feeder position" );
               feederPositionActive = false;
               shooter.disablePIDFeederPosition();

               lastFeederPosition = FeederPosition.UNSET;
            }

            shooter.rotateFeeder( -1.0 );
         }
      }
      else if ( !isFire && isKickOut )
      {
         shooter.eject( defaultShotSpeed );
      }
      else
      {
         final double shotSpeed = getShotSpeed();
         shooter.shoot( shotSpeed );

         feederSpeed = getFeederSpeed();
         if ( Math.abs( feederSpeed ) < 0.15 )
         {
            logger.trace( "run feeder position" );
            feederPosition = getFeederPosition();
            if ( feederPosition != FeederPosition.UNSET )
            {
               if ( !feederPositionActive )
               {
                  logger.debug( "toggling to enable feeder position" );
                  feederSpeed = 0.0;
                  shooter.rotateFeeder( feederSpeed );

                  feederPositionActive = true;
                  shooter.enablePIDFeederPosition();
               }
               if ( feederPosition != lastFeederPosition )
               {
                  logger.debug( "set feeder position to: {}", feederPosition );
                  lastFeederPosition = feederPosition;

                  shooter.setFeederPosition( feederPosition );
               }
            }
            else if ( !feederPositionActive )
            {
               logger.trace( "UNSET & inactive - run feeder speed" );
               shooter.rotateFeeder( feederSpeed );
            }
         }
         else
         {
            logger.trace( "run feeder speed" );
            if ( feederPositionActive )
            {
               logger.debug( "toggling to disable feeder position" );
               feederPositionActive = false;
               shooter.disablePIDFeederPosition();

               lastFeederPosition = FeederPosition.UNSET;
            }

            shooter.rotateFeeder( feederSpeed );
         }

         // If here, we are out of the button-based shooting
         shootTimer.stop();
         shootTimer.reset();
      }
   }


   private boolean isKickOutButtonPressed()
   {
      final boolean pressed = joystick.getRawButton( kickOutButton );
      return pressed;
   }


   private boolean isFireButtonPressed()
   {
      final boolean pressed = joystick.getRawButton( fireButton );
      return pressed;
   }


   private double getShotSpeed()
   {
      final double speed = joystick.getRawAxis( shotSpeedAxis );
      smartDashboard.putNumber( "hmiShotSpeed", speed );
      if ( isZero( speed ) )
      {
         return 0.0;
      }
      else
      {
         return adjustShotSpeedInputs( speed );
      }
   }


   private double adjustShotSpeedInputs( double speed )
   {
      // No flattening of response curve for this

      // No turbo for this

      // Cap at the max value
      speed = Math.min( speed, defaultShotSpeed );

      return adjustShotSpeedSign( speed );
   }


   /**
    * Ensure the sign of the speed is correct for robot convention; namely
    * "out"/"shoot" is positive, and "in"/"eject" is negative.
    *
    * @param speed
    * @return
    **/
   private double adjustShotSpeedSign( double speed )
   {
      return speed;
   }


   private FeederPosition getFeederPosition()
   {
      final int anglePOV = joystick.getPOV( feederPositionPOVIndex );
      if ( anglePOV == -1 )
      {
         return FeederPosition.UNSET;
      }
      else if ( anglePOV == 0 )
      {
         return FeederPosition.UP;
      }
      else if ( anglePOV == 45 )
      {
         return FeederPosition.UP_FRONT;
      }
      else if ( anglePOV == 90 )
      {
         return FeederPosition.FRONT;
      }
      else if ( anglePOV == 135 )
      {
         return FeederPosition.DOWN_FRONT;
      }
      else if ( anglePOV == 180 )
      {
         return FeederPosition.DOWN;
      }
      else if ( anglePOV == 225 )
      {
         return FeederPosition.DOWN_REAR;
      }
      else if ( anglePOV == 270 )
      {
         return FeederPosition.REAR;
      }
      else if ( anglePOV == 315 )
      {
         return FeederPosition.UP_REAR;
      }
      else
      {
         return FeederPosition.UNSET;
      }
   }


   private double getFeederSpeed()
   {
      // FIXME - Negative added by Adam
      final double speed = -( joystick.getRawAxis( feederSpeedAxis ) );
      smartDashboard.putNumber( "hmiFeederSpeed", speed );
      if ( isZero( speed ) )
      {
         return 0.0;
      }
      else
      {
         return adjustFeederSpeedInputs( speed );
      }
   }


   private double adjustFeederSpeedInputs( double speed )
   {
      // No flattening of response curve for this

      // No turbo for this

      return adjustFeederSpeedSign( speed );
   }


   /**
    * Ensure the sign of the speed is correct for robot convention; namely
    * "feed" is positive, and "reverse" is negative.
    *
    * @param speed
    * @return
    **/
   private double adjustFeederSpeedSign( double speed )
   {
      return -speed;
   }


   /**
    * Implements the hanging functionality.
    **/
   private void processHanging()
   {
      final boolean isLatch = isLatchButtonPressed();
      final boolean isLift = isLiftButtonPressed();
      final boolean isLiftEnabled = isLiftEnableButtonPressed();
      logger.trace( "hanging: isLatch = {}, isLift = {}, isLiftEnabled",
         isLatch, isLift, isLiftEnabled );

      // Starts by pressing latch (which raises hanger hook)
      if ( isLatch && !isLift )
      {
         if ( hangTimer.get() == 0.0 )
         {
            hangTimer.start();
            doingHanging = true;
            setDoingHanging( doingHanging );

            // Drive it hard to start
            lifter.hang( 0.80 );
         }
         else if ( hangTimer.get() > 2.0 )
         {
            lifter.hang( 0.35 ); // Lower speed to complete // 0.30

            // Override of Driver controls during end state
            // TODO - Shouldn't this be as soon as we start?
            // Keep it up against tower
            drive.driveStraight( -0.50 ); // 0.60 // 0.70 // 0.65
         }
      }
      // Once latched, and then lift the robot
      else if ( isLatch && isLift )
      {
         // Wait for Driver to confirm
         if ( !liftEnabled && isLiftEnabled )
         {
            liftEnabled = true;
            setLiftEnabled( liftEnabled );
         }
         // Once confirmed, we process the lifting
         if ( liftEnabled )
         {
            // Lift the robot
            lifter.liftRobot();
            // We keep driving the hanger anyway
            lifter.hang( 0.35 ); // 0.30

            // Override of Driver controls during end state
            // Keep it in position as lifting
            drive.driveStraight( -0.30 );
         }
      }
      // All done, or failed attempt - stop everything
      else if ( doingHanging )
      {
         hangTimer.stop();
         hangTimer.reset();

         // lifter.stopHang();
         lifter.hang( 0.30 );
         lifter.stopLift( false );

         // Override of Driver controls during end state
         drive.stopDrivingStraight();

         doingHanging = false;
         setDoingHanging( doingHanging );

         liftEnabled = false;
         setLiftEnabled( liftEnabled );
      }
   }


   /**
    * Reads the value of the <i>Latch</i> button from the joystick.
    *
    * @return <code>true</code> if button is pressed; <code>false</code>
    *         otherwise.
    **/
   private boolean isLatchButtonPressed()
   {
      final boolean pressed = joystick.getRawButton( latchButton );
      return pressed;
   }


   /**
    * Reads the value of the <i>Lift</i> button from the joystick.
    *
    * @return <code>true</code> if button is pressed; <code>false</code>
    *         otherwise.
    **/
   private boolean isLiftButtonPressed()
   {
      final boolean pressed = joystick.getRawButton( liftButton );
      return pressed;
   }


   /**
    * Reads the value of the <i>LiftEnabled</i> button from the <i>Driver</i>
    * joystick.
    *
    * @return <code>true</code> if button is pressed; <code>false</code>
    *         otherwise.
    **/
   private boolean isLiftEnableButtonPressed()
   {
      final boolean pressed1 = driverJoystick.getRawButton( liftEnableButton1 );
      final boolean pressed2 = driverJoystick.getRawButton( liftEnableButton2 );
      return pressed1 || pressed2;
   }

}
