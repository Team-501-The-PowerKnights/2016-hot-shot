/**
 *
 */

package com.powerknights.frc2016.robot.modules;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.config.ShooterConfig;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SpeedController;


/**
 * This module handles the primary shooter mechanism, the motors (left and
 * right) which spin the wheels to cause the ball to either "shoot" out the top,
 * or be "ejected" (reversed to come back out the bottom).
 *
 * @author first.tony
 */
public class ShooterModule
   extends PWMModule
{

   /** Our class's logger */
   private static final Logger logger =
      RioLogger.getLogger( ShooterModule.class.getName() );

   /** Singleton instance of class for all to use **/
   private static ShooterModule ourInstance;
   /** Name of our module **/
   private static final String myName = "ShooterModule";


   /**
    * Constructs instance of the shooter module. Assumed to be called before any
    * usage of the module; and verifies only called once. Allows controlled
    * startup sequencing of the robot and all it's module.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( myName + " Already Constructed" );
      }
      ourInstance = new ShooterModule();
   }


   /**
    * Returns the singleton instance of the shooter module. If it hasn't been
    * constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of the shooter module
    **/
   public static ShooterModule getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( myName + " Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Speed controller for left side **/
   private final SpeedController leftMotor;
   /** SmartDash component name for motor **/
   private final String leftMotorName = "leftMotor";
   /** Speed controller for right side **/
   private final SpeedController rightMotor;
   /** SmartDash component name for motor **/
   private final String rightMotorName = "rightMotor";

   /** Telemetry state name for motor **/
   private final String leftMotorTelemetryState = "leftShooterMotor";
   /** Telemetry speed name for motor **/
   private final String leftMotorTelemetrySpeed = "leftShooterSpeed";
   /** Telemetry state name for motor **/
   private final String rightMotorTelemetryState = "rightShooterMotor";
   /** Telemetry speed name for motor **/
   private final String rightMotorTelemetrySpeed = "rightShooterSpeed";


   private ShooterModule()
   {
      logger.info( "constructing" );

      /*
       * Speed Controllers
       */
      leftMotor = createLeftMotor();
      rightMotor = createRightMotor();

      logger.info( "constructed" );
   }


   /***
    * Constructs/initializes the left speed controller
    *
    * @return constructed and initialized motor
    */
   private SpeedController createLeftMotor()
   {
      final int canID = ShooterConfig.getLeftMotorChannel();
      final CANTalon sc = new CANTalon( canID );
      final String component = leftMotorName + "[" + canID + "]";
      liveWindow.addActuator( myName, component, sc );
      return sc;
   }


   /***
    * Constructs/initializes the right speed controller
    *
    * @return constructed and initialized motor
    */
   private SpeedController createRightMotor()
   {
      final int canID = ShooterConfig.getRightMotorChannel();
      final CANTalon sc = new CANTalon( canID );
      final String component = rightMotorName + "[" + canID + "]";
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

   private void setLeftMotorRunning( boolean motor )
   {
      smartDashboard.putBoolean( leftMotorTelemetryState, motor );
   }


   private void setRightMotorRunning( boolean motor )
   {
      smartDashboard.putBoolean( rightMotorTelemetryState, motor );
   }

   /***********************************
    * Public Control Section
    **********************************/


   /**
    * Stop the shooter.
    */
   public void stop()
   {
      // Do not use "stopMotor" method

      setMotorSpeeds( 0.0, 0.0 );
   }


   /**
    * Shoot the ball. Negative values, which would normally imply ejecting the
    * ball back out, are normalized to positive values first - so the ball
    * always will shoot.
    *
    * Stopping the shooter is not handled by this method, unless by chance.
    *
    * @param speed
    */
   public void shoot( double speed )
   {
      speed = ensurePositiveSpeed( speed );
      setMotorSpeeds( fixLeftPolarity( speed ), fixRightPolarity( speed ) );
   }


   /**
    * Eject the ball. Positive values, which would normally imply shooting the
    * ball out the top, are normalized to negative values first - so the ball
    * always will eject.
    *
    * Stopping the shooter is not handled by this method, unless by chance.
    *
    * @param speed
    */
   public void eject( double speed )
   {
      speed = ensureNegativeSpeed( speed );
      setMotorSpeeds( fixLeftPolarity( speed ), fixRightPolarity( speed ) );
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is out and "-"
    * (negative) is in.
    *
    * @param speed
    * @return
    */
   private double fixLeftPolarity( double speed )
   {
      // This is where you change the sign to fix polarity of shooter if
      // necessary
      // Left is + out and - in
      return -speed;
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is out and "-"
    * (negative) is in.
    *
    * @param speed
    * @return
    */
   private double fixRightPolarity( double speed )
   {
      // This is where you change the sign to fix polarity of shooter if
      // necessary
      // Right is - out and + in
      return -speed;
   }


   /**
    * Sets the speed / inputs to the actual hardware speed controllers for the
    * left and right motors.
    *
    * @paramleftSpeed - left side setting for speed controller(s)
    * @param rightSpeed - right side setting for speed controller(s)
    */
   private void setMotorSpeeds( double leftSpeed, double rightSpeed )
   {
      smartDashboard.putNumber( leftMotorTelemetrySpeed, leftSpeed );
      setLeftMotorRunning( !isZero( leftSpeed ) );
      smartDashboard.putNumber( rightMotorTelemetrySpeed, rightSpeed );
      setRightMotorRunning( !isZero( rightSpeed ) );

      leftMotor.set( leftSpeed );
      rightMotor.set( rightSpeed );
   }

}
