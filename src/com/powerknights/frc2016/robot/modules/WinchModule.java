/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.modules;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.config.LifterConfig;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SpeedController;


/**
 * @author first.stu
 **/
public class WinchModule
   extends PWMModule
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( WinchModule.class.getName() );

   /** Singleton instance of class for all to use **/
   private static WinchModule ourInstance;
   /** Name of our module **/
   private static final String myName = "WinchModule";


   /**
    * Constructs instance of the winch module. Assumed to be called before any
    * usage of the module; and verifies only called once. Allows controlled
    * startup sequencing of the robot and all it's module.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( myName + " Already Constructed" );
      }
      ourInstance = new WinchModule();
   }


   /**
    * Returns the singleton instance of the winch module. If it hasn't been
    * constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of winch
    **/
   public static WinchModule getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( myName + " Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Speed controller for left side winch **/
   private final SpeedController leftMotor;
   /** SmartDash component name for motor **/
   private final String leftMotorName = "leftMotor";
   /** Speed controller for right side winch **/
   private final SpeedController rightMotor;
   /** SmartDash component name for motor **/
   private final String rightMotorName = "rightMotor";
   /** Speed controller for left side winch aux **/
   private final SpeedController leftAuxMotor;
   /** SmartDash component name for motor **/
   private final String leftAuxMotorName = "leftAuxMotor";
   /** Speed controller for right side winch aux **/
   private final SpeedController rightAuxMotor;
   /** SmartDash component name for motor **/
   private final String rightAuxMotorName = "rightAuxMotor";

   /** **/
   private final String leftMotorsTelemetryState = "leftWinchMotor(s)";
   /** **/
   private final String leftMotorsTelemetrySpeed = "leftWinchSpeed";
   /** **/
   private final String rightMotorsTelemetryState = "rightWinchMotor(s)";
   /** **/
   private final String rightMotorsTelemetrySpeed = "rightWinchSpeed";


   private WinchModule()
   {
      logger.info( "constructing" );

      /*
       * Speed Controllers
       */
      leftMotor = constructLeftMotor();
      rightMotor = constructRightMotor();

      leftAuxMotor = constructLeftAuxMotor();
      rightAuxMotor = constructRightAuxMotor();

      logger.info( "constructed" );
   }


   /**
    * Constructs/initializes the left speed controller
    *
    * @return constructed and initialized motor
    **/
   private SpeedController constructLeftMotor()
   {
      final int canID = LifterConfig.getLeftWinchMotorId();
      final CANTalon sc = new CANTalon( canID );
      final String component = leftMotorName + "[" + canID + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /**
    * Constructs/initializes the right speed controller
    *
    * @return constructed and initialized motor
    **/
   private SpeedController constructRightMotor()
   {
      final int canID = LifterConfig.getRightWinchMotorId();
      final CANTalon sc = new CANTalon( canID );
      final String component = rightMotorName + "[" + canID + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /**
    * Constructs/initializes the left aux speed controller
    *
    * @return constructed and initialized motor
    **/
   private SpeedController constructLeftAuxMotor()
   {
      final int canID = LifterConfig.getLeftWinchAuxMotorId();
      final CANTalon sc = new CANTalon( canID );
      final String component = leftAuxMotorName + "[" + canID + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /**
    * Constructs/initializes the right aux speed controller
    *
    * @return constructed and initialized motor
    **/
   private SpeedController constructRightAuxMotor()
   {
      final int canID = LifterConfig.getRightWinchAuxMotorId();
      final CANTalon sc = new CANTalon( canID );
      final String component = rightAuxMotorName + "[" + canID + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.modules.IModule#reset()
    */
   @Override
   public void reset()
   {
      stop();
   }


   /***********************************
    * State Management Section
    ***********************************/

   @SuppressWarnings( "unused" )
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
    ***********************************/

   /**
    * Stop the winch moving.
    **/
   public void stop()
   {
      // Do not use "stopMotor" method

      setMotorSpeeds( 0.0, 0.0 );
   }


   /**
    * Winch / pull the line in. Negative values, which would normally imply
    * letting the line out, are normalized to a positive value first - so the
    * winch mechanism will always go in.
    *
    * Stopping the lift is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void in( double speed )
   {
      speed = ensurePositiveSpeed( speed );

      setMotorSpeeds( fixLeftPolarity( speed ), fixRightPolarity( speed ) );
   }


   /**
    * Winch / release the line out. Positive values, which would normally imply
    * pulling the line in, are normalized to a negative value first - so the
    * winch will always go out.
    *
    * Stopping the lift is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void out( double speed )
   {
      speed = ensureNegativeSpeed( speed );

      setMotorSpeeds( fixLeftPolarity( speed ), fixRightPolarity( speed ) );
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is up and "-"
    * (negative) is down relative to winch of robot.
    *
    * @param speed
    * @return
    **/
   private double fixLeftPolarity( double speed )
   {
      // This is where you change the sign to fix polarity of lift if necessary
      // Left is + up and - down
      return speed;
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is "up" and "-"
    * (negative) is "down" relative to winch of robot.
    *
    * @param speed
    * @return
    **/
   private double fixRightPolarity( double speed )
   {
      // This is where you change the sign to fix polarity of lift if necessary
      // Right is - up and + down
      return -speed;
   }


   /**
    * Sets the speed / inputs to the actual hardware speed controllers for the
    * left and right winches. Includes updates to the dashboard.
    *
    * @paramleftSpeed - left side setting for speed controller(s)
    * @param rightSpeed - right side setting for speed controller(s)
    **/
   private void setMotorSpeeds( double leftSpeed, double rightSpeed )
   {
      smartDashboard.putNumber( leftMotorsTelemetrySpeed, leftSpeed );
      setLeftMotorsRunning( !isZero( leftSpeed ) );
      smartDashboard.putNumber( rightMotorsTelemetrySpeed, rightSpeed );
      setRightMotorsRunning( !isZero( rightSpeed ) );

      leftMotor.set( leftSpeed );
      leftAuxMotor.set( leftSpeed );
      rightMotor.set( rightSpeed );
      rightAuxMotor.set( rightSpeed );
   }

}
