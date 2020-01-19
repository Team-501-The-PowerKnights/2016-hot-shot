/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
 **/

package com.powerknights.frc2016.robot.subsystems;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.managers.PreferencesManager;
import com.powerknights.frc2016.robot.vision.USBCameraVision;


/**
 * @author first.stu
 **/
public class Chassis
   extends PWMSubsystem
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( Chassis.class.getName() );


   static
   {
      // LOGGER - Override of default level
      // RioLogger.setLevel( logger, Level.TRACE );
   }

   /** Singleton instance of class for all to use **/
   private static Chassis ourInstance;
   /** Name of our subsystem **/
   private static final String myName = "Chassis";


   /**
    * Constructs instance of the chassis subsystem. Assumed to be called before
    * any usage of the subsystem; and verifies only called once. Allows
    * controlled startup sequencing of the robot and all it's subsystems.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( "Chassis Already Constructed" );
      }
      ourInstance = new Chassis();
   }


   /**
    * Returns the singleton instance of the chassis subsystem. If it hasn't been
    * constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of chassis
    **/
   public static Chassis getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( "Chassis Not Constructed Yet" );
      }
      return ourInstance;
   }

   public enum DriveMode
   {
      Stop, Straight, Rotate, Drive, Unknown
   };

   /** State of the chassis subsystem **/
   private DriveMode driveMode;

   /** Handle to drive subsystem **/
   private final DriveTrain drive;

   /** Server for streaming camera data for dashbaord **/
   @SuppressWarnings( "unused" )
   private final USBCameraVision camera;

   private boolean driving;
   private boolean started;


   private Chassis()
   {
      logger.info( "constructing" );

      // Assumes that drive is constructed first
      drive = DriveTrain.getInstance();

      smartDashboard.putString( "distSource", "" );

      camera = constructDashCamera();

      logger.info( "constructed" );
   }


   /**
    * Constructs/starts the camera used for dashboard general vision
    *
    * @return constructed and started camera if enabled; <code>null</code>
    *         otherwise
    **/
   private USBCameraVision constructDashCamera()
   {
      if ( !PreferencesManager.getInstance().runDashCamera() )
      {
         logger.info( "dashCam not enabled" );
         return null;
      }

      final String device = PreferencesManager.getInstance().getUsbDashCamera();
      final USBCameraVision camera = new USBCameraVision( device );
      camera.start();
      logger.info( "dashCam {} initialized and started", device );
      return camera;
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.subsystems.ISubsystem#reset()
    */
   @Override
   public void reset()
   {
      stopDriving();
   }


   /**
    *
    * @return distance to tower in decimal feet
    **/
   public double getDistanceToTower()
   {
      // final double farDistance = farWallDistance.get();
      // final boolean farValid = farWallDistance.isValidRange( farDistance );
      //
      // double retValue;
      // if ( farValid )
      // {
      // smartDashboard.putString( "distSource", "farWall" );
      // retValue = farDistance;
      // }
      // else
      // {
      // smartDashboard.putString( "distSource", "" );
      // retValue = -1.0;
      // }
      // return retValue;

      return 15.0;
   }


   public boolean inRange()
   {
      final boolean result = true;
      smartDashboard.putBoolean( "atShotDistance", result );
      return true;
   }


   /*
    * State Management Section
    */

   private void setDriveMode( DriveMode mode )
   {
      if ( driveMode != mode )
      {
         driveMode = mode;
         // TODO - Name conflict with 'driveMode'
         smartDashboard.putString( "driveMode", driveMode.name() );
      }
   }


   private void stop()
   {
      drive.stop();
   }


   public synchronized void startDriving()
   {
      setDriving( true );
      setStarted( false );
   }


   public boolean isDriving()
   {
      return driving;
   }


   private void setDriving( boolean driving )
   {
      this.driving = driving;
      smartDashboard.putBoolean( "driving", driving );
   }


   private boolean isStarted()
   {
      return started;
   }


   private void setStarted( boolean started )
   {
      this.started = started;
      smartDashboard.putBoolean( "started", started );
   }


   public synchronized void stopDriving()
   {
      stop();
      setDriving( false );
      setStarted( false );
   }


   /**
    * Sets the individual speeds (left and right) on the underlying
    * <code>DriveTrain</code> itself. Assumes the relative values and signs have
    * been correctly determined to conform to the <code>DriveTrain</code>
    * conventions. This is currently "positive" values going forward, and
    * "negative" values going backwards.
    *
    * @param leftSpeed
    * @param rightSpeed
    **/
   private void setDriveSpeeds( double leftSpeed, double rightSpeed )
   {
      // TODO - Figure out what needs to be here for this year
      drive.arcadeDrive( leftSpeed, rightSpeed );
      // drive.robotDrive( leftSpeed, 0.0, 0.0 );
   }


   /**
    * Drives the robot in an 'arc', with a combination of forward direction and
    * rotation. Convention for input is "positive" values go forward, and
    * "negative" values go backwards.
    *
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param leftSpeed
    * @param rightSpeed
    **/
   public void drive( double leftSpeed, double rightSpeed )
   {
      setDriveSpeeds( leftSpeed, rightSpeed );
   }


   /**
    * Drive the robot in a 'straight line', with no turn. Convention for input
    * is "positive" values go forward, and "negative" values go backwards.
    *
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void drive( double speed )
   {
      if ( speed >= 0.0 )
      {
         driveForward( speed );
      }
      else
      {
         driveBackward( speed );
      }
   }


   /**
    * Drive the robot forward, in a 'straight line' with no turn. Negative
    * values, which would normally imply driving backwards, are normalized to a
    * positive value first - so the robot will always go forward.
    *
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void driveForward( double speed )
   {
      speed = ensurePositiveSpeed( speed );

      driveStraight( speed );
   }


   /**
    * Drive the robot backward, in a 'straight line' with no turn. Positive
    * values, which would normally imply driving forwards, are normalized to a
    * negative value first - so the robot will always go backward.
    *
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void driveBackward( double speed )
   {
      speed = ensureNegativeSpeed( speed );

      driveStraight( speed );
   }


   /**
    * Drive the robot in a straight line, where the left and right speeds are
    * locked together. Convention for input is "positive" values go forward, and
    * "negative" values go backwards.
    *
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   private void driveStraight( double speed )
   {
      if ( !isDriving() )
      {
         logger.error( "driving not started / enabled" );
         return;
      }

      if ( !isStarted() )
      {
         setStarted( true );
         setDriveMode( DriveMode.Straight );
      }
      setDriveSpeeds( speed, speed );
   }


   /**
    * Rotate the robot about the centerpoint, by turning in-place. The direction
    * of rotation is counter-clockwise as viewed from above. The same speed is
    * used to drive both sides, just in opposite direction of travel.
    *
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void rotateCCW( double speed )
   {
      setDriveMode( DriveMode.Rotate );

      // left backward, right forward
      setDriveSpeeds( ensureNegativeSpeed( speed ),
         ensurePositiveSpeed( speed ) );
   }


   /**
    * Rotate the robot about the centerpoint, by turning in-place. The direction
    * of rotation is clockwise as viewed from above. The same speed is used to
    * drive both sides, just in opposite direction of travel.
    *
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void rotateCW( double speed )
   {
      setDriveMode( DriveMode.Rotate );

      // left forward, right backward
      setDriveSpeeds( ensurePositiveSpeed( speed ),
         ensureNegativeSpeed( speed ) );
   }


   public void goForward( double speed, double distance )
   {
      setDriveMode( DriveMode.Straight );

      speed = ensurePositiveSpeed( speed );
      // TODO - Implement goForward() in DriveTrain
      logger.error( "goForward not implemented" );
   }


   public void goBackward( double speed, double distance )
   {
      setDriveMode( DriveMode.Straight );

      speed = ensureNegativeSpeed( speed );
      // TODO - Implement goBackward() in DriveTrain
      logger.error( "goBackward not implemented" );
   }

}
