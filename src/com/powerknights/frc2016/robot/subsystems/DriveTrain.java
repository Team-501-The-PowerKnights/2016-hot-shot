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
import com.powerknights.frc2016.robot.config.DriveTrainConfig;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.VictorSP;


/**
 * @author first.cody
 **/
public class DriveTrain
   extends PWMSubsystem
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( DriveTrain.class.getName() );

   /** Singleton instance of class for all to use **/
   private static DriveTrain ourInstance;
   /** Name of our subsystem **/
   private static final String myName = "DriveTrain";


   /**
    * Constructs instance of the drive train subsystem. Assumed to be called
    * before any usage of the subsystem; and verifies only called once. Allows
    * controlled startup sequencing of the robot and all it's subsystems.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( "DriveTrain Already Constructed" );
      }
      ourInstance = new DriveTrain();
   }


   /**
    * Returns the singleton instance of the drive train subsystem. If it hasn't
    * been constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of drive train
    **/
   public static DriveTrain getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( "DriveTrain Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Speed controller for left front side **/
   private final SpeedController leftFrontMotor;
   /** SmartDash component name for motor **/
   private final String leftFrontMotorName = "leftFrontMotor";
   /** Speed controller for left rear side **/
   private final SpeedController leftRearMotor;
   /** SmartDash component name for motor **/
   private final String leftRearMotorName = "leftRearMotor";
   /** Speed controller for right front side **/
   private final SpeedController rightFrontMotor;
   /** SmartDash component name for motor **/
   private final String rightFrontMotorName = "rightFrontMotor";
   /** Speed controller for right rear side **/
   private final SpeedController rightRearMotor;
   /** SmartDash component name for motor **/
   private final String rightRearMotorName = "rightRearMotor";

   /** Telemetry state name for motor **/
   private final String leftMotorsTelemetryState = "leftDriveMotor(s)";
   /** Telemetry speed name for motor **/
   private final String leftMotorsTelemetrySpeed = "leftDriveSpeed";

   /** Telemetry state name for motor **/
   private final String rightMotorsTelemetryState = "rightDriveMotor(s)";
   /** Telemetry speed name for motor **/
   private final String rightMotorsTelemetrySpeed = "rightDriveSpeed";

   /** WPI's drive for non-straight or rotate **/
   private final RobotDrive robotDrive;


   private DriveTrain()
   {
      logger.info( "constructing" );

      /*
       * Speed Controllers
       */
      leftFrontMotor = constructLeftFrontMotor();
      leftRearMotor = constructLeftRearMotor();
      rightFrontMotor = constructRightFrontMotor();
      rightRearMotor = constructRightRearMotor();

      /*
       * Drive
       */
      robotDrive = constructRobotDrive();

      logger.info( "constructed" );
   }


   /**
    * Constructs/initializes the left front drive speed controller
    *
    * @return constructed and initialized motor
    */
   private SpeedController constructLeftFrontMotor()
   {
      final int channel = DriveTrainConfig.getLeftFrontMotorChannel();
      final VictorSP sc = new VictorSP( channel );
      final String component = leftFrontMotorName + "[" + channel + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /**
    * Constructs/initializes the left rear drive speed controller
    *
    * @return constructed and initialized motor
    */
   private SpeedController constructLeftRearMotor()
   {
      final int channel = DriveTrainConfig.getLeftRearMotorChannel();
      final VictorSP sc = new VictorSP( channel );
      final String component = leftRearMotorName + "[" + channel + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /**
    * Constructs/initializes the right front drive speed controller
    *
    * @return constructed and initialized motor
    */
   private SpeedController constructRightFrontMotor()
   {
      final int channel = DriveTrainConfig.getRightFrontMotorChannel();
      final VictorSP sc = new VictorSP( channel );
      final String component = rightFrontMotorName + "[" + channel + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /**
    * Constructs/initializes the right rear drive speed controller
    *
    * @return constructed and initialized motor
    */
   private SpeedController constructRightRearMotor()
   {
      final int channel = DriveTrainConfig.getRightRearMotorChannel();
      final VictorSP sc = new VictorSP( channel );
      final String component = rightRearMotorName + "[" + channel + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /**
    * Constructs/initializes the robot drive to be used for non-straight and
    * non-rotate driving.
    *
    * @return constructed and initialized drive
    **/
   private RobotDrive constructRobotDrive()
   {
      final RobotDrive rd = new RobotDrive( leftFrontMotor, leftRearMotor,
         rightFrontMotor, rightRearMotor );

      // We do our own control
      rd.setSafetyEnabled( false );

      return rd;
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2013.subsystems.ISubsystem#reset()
    */
   @Override
   public void reset()
   {
      stop();
   }


   /***********************************
    * State Management Section
    ***********************************/

   private void setMotorsRunning( boolean leftMotor, boolean rightMotor )
   {
      setLeftMotorsRunning( leftMotor );
      setRightMotorsRunning( rightMotor );
   }


   private void setLeftMotorsRunning( boolean motor )
   {
      smartDashboard.putBoolean( leftMotorsTelemetryState, motor );
   }


   private void setRightMotorsRunning( boolean motor )
   {
      smartDashboard.putBoolean( rightMotorsTelemetryState, motor );
   }


   /***********************************
    * Public Control Section
    **********************************/

   /**
    * Drive the robot using 'tank' mode, where the left and right speed are
    * controlled and provided independently.
    * <p>
    * Convention for input is "positive" values go forward, and "negative"
    * values go backwards. The proper translation to inputs to the motors is
    * handled internally.
    * <p>
    * Values not within the range -1.0 <= value <= +1.0 are capped before
    * passing to the underlying hardware.
    * <p>
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param leftSpeed
    * @param rightSpeed
    **/
   public void tankDrive( double leftSpeed, double rightSpeed )
   {
      setMotorsRunning( true, true );

      leftSpeed = capSpeed( leftSpeed );
      rightSpeed = capSpeed( rightSpeed );

      setMotorSpeeds( fixLeftPolarity( leftSpeed ),
         fixRightPolarity( rightSpeed ) );
   }

   private boolean driveOverride = false;


   public void driveStraight( double speed )
   {
      driveOverride = true;

      setMotorsRunning( true, true );

      speed = capSpeed( speed );
      final double rotation = 0.0;

      double leftSpeed;
      double rightSpeed;
      if ( speed > 0.0 )
      {
         leftSpeed = Math.max( speed, -rotation );
         rightSpeed = speed + rotation;
      }
      else
      {
         leftSpeed = speed - rotation;
         rightSpeed = -Math.max( -speed, -rotation );
      }

      setMotorSpeeds( fixLeftPolarity( leftSpeed ),
         fixRightPolarity( rightSpeed ) );
   }


   /**
    * Stop the robot driving.
    **/
   public void stopDrivingStraight()
   {
      driveOverride = false;

      // Do not use "stopMotor" method

      setMotorSpeeds( 0.0, 0.0 );
   }


   /**
    * Implements 'arcade mode' style of driving using the our own underlying
    * implementation.
    * <p>
    * Convention for input is "positive" values go forward (<code>speed</code>)
    * or to the right ( <code>rotation</code>), and "negative" values go
    * backwards (<code>speed</code>) and left ( <code>rotation</code>). The
    * proper translation to inputs to the motors is handled internally.
    * <p>
    * Values not within the range -1.0 <= value <= +1.0 are capped before
    * passing to the underlying hardware.
    * <p>
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param speed
    * @param rotation
    **/
   public void arcadeDrive( double speed, double rotation )
   {
      if ( driveOverride )
      {
         return;
      }

      setMotorsRunning( true, true );

      speed = capSpeed( speed );
      rotation = capSpeed( rotation );

      double leftSpeed;
      double rightSpeed;
      if ( speed > 0.0 )
      {
         if ( rotation > 0.0 )
         {
            leftSpeed = speed - rotation;
            rightSpeed = Math.max( speed, rotation );
         }
         else
         {
            leftSpeed = Math.max( speed, -rotation );
            rightSpeed = speed + rotation;
         }
      }
      else
      {
         if ( rotation > 0.0 )
         {
            leftSpeed = -Math.max( -speed, rotation );
            rightSpeed = speed + rotation;
         }
         else
         {
            leftSpeed = speed - rotation;
            rightSpeed = -Math.max( -speed, -rotation );
         }
      }

      setMotorSpeeds( fixLeftPolarity( leftSpeed ),
         fixRightPolarity( rightSpeed ) );
   }


   /**
    * Implements 'arcade mode' style of driving using the underlying WPILib
    * implementation.
    * <p>
    * Convention for input is "positive" values go forward or to the right, and
    * "negative" values go backwards (<code>speed</code>) and left (
    * <code>rotation</code>. The proper translation to inputs to the motors is
    * handled internally.
    * <p>
    * Values not within the range -1.0 <= value <= +1.0 are capped before
    * passing to the underlying hardware.
    * <p>
    * Stopping the robot is not handled by this method, unless by chance.
    *
    * @param speed
    * @param rotation
    **/
   public void arcadeDriveWPI( double speed, double rotation )
   {
      setMotorsRunning( true, true );

      speed = capSpeed( speed );
      rotation = capSpeed( rotation );

      // TODO - fix polarity tweak
      robotDrive.arcadeDrive( speed, -rotation );
   }


   /**
    * Implements polar 'mecanum mode' style of driving using the underlying
    * WPILib implementation.
    *
    * @param speed
    * @param strafe
    * @param rotation
    **/
   public void mecanumDriveWPI( double speed, double strafe, double rotation )
   {
      setMotorsRunning( true, true );

      speed = capSpeed( speed );
      smartDashboard.putNumber( "driveSpeed", speed );
      strafe = capSpeed( strafe );
      smartDashboard.putNumber( "driveStrafe", strafe );
      rotation = capSpeed( rotation );
      smartDashboard.putNumber( "driveRotation", rotation );

      // HACKALERT - Switch speed sign (don't feed joysticks directly)
      robotDrive.mecanumDrive_Cartesian( strafe, -speed, rotation, 0.0 );
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is "forward" and
    * "-" (negative) is "backwards" relative to drive train.
    *
    * @param speed
    * @return
    **/
   private double fixLeftPolarity( double speed )
   {
      // This is where you change the sign to fix polarity of drive if necessary
      // Left is + forward and - backward
      return +speed;
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is "forward" and
    * "-" (negative) is "backwards" relative to drive train.
    *
    * @param speed
    * @return
    **/
   private double fixRightPolarity( double speed )
   {
      // This is where you change the sign to fix polarity of drive if necessary
      // Right is - forward and + backward
      return -speed;
   }


   /**
    * Stop the robot driving.
    **/
   public void stop()
   {
      if ( driveOverride )
      {
         return;
      }

      // Do not use "stopMotor" method

      setMotorSpeeds( 0.0, 0.0 );
   }


   /**
    * Sets the speed / inputs to the actual hardware speed controllers for the
    * left and right drives. Includes update to the dashboard.
    *
    * @param leftSpeed - left side setting for speed controller(s)
    * @param rightSpeed - right side setting for speed controller(s)
    **/
   private void setMotorSpeeds( double leftSpeed, double rightSpeed )
   {
      smartDashboard.putNumber( leftMotorsTelemetrySpeed, leftSpeed );
      setLeftMotorsRunning( !isZero( leftSpeed ) );
      smartDashboard.putNumber( rightMotorsTelemetrySpeed, rightSpeed );
      setRightMotorsRunning( !isZero( rightSpeed ) );

      leftFrontMotor.set( leftSpeed );
      leftRearMotor.set( leftSpeed );
      rightFrontMotor.set( rightSpeed );
      rightRearMotor.set( rightSpeed );
   }

}
