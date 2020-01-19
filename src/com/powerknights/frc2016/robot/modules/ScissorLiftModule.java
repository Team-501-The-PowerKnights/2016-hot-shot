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
public class ScissorLiftModule
   extends PWMModule
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( ScissorLiftModule.class.getName() );

   /** Singleton instance of class for all to use **/
   private static ScissorLiftModule ourInstance;
   /** Name of our module **/
   private static final String myName = "ScissorLiftModule";


   /**
    * Constructs instance of the scissor lift module. Assumed to be called
    * before any usage of the module; and verifies only called once. Allows
    * controlled startup sequencing of the robot and all it's module.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( myName + " Already Constructed" );
      }
      ourInstance = new ScissorLiftModule();
   }


   /**
    * Returns the singleton instance of the scissor lift module. If it hasn't
    * been constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of scissor lift
    **/
   public static ScissorLiftModule getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( myName + " Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Speed controller for scissor lift **/
   private final SpeedController motor;
   /** SmartDash component name for motor **/
   private final String leftMotorName = "motor";

   /** **/
   private final String telemetryStateName = "scissorLiftMotor";
   /** **/
   private final String telemetrySpeedName = "scissorLiftSpeed";


   private ScissorLiftModule()
   {
      logger.info( "constructing" );

      /*
       * Speed Controllers
       */
      motor = constructMotor();

      logger.info( "constructed" );
   }


   /**
    * @return
    **/
   private SpeedController constructMotor()
   {
      final int canID = LifterConfig.getScissorLiftMotorChannel();
      final CANTalon sc = new CANTalon( canID );
      final String component = leftMotorName + "[" + canID + "]";
      liveWindow.addActuator( myName, component, sc );

      // Set the brake on the feeder
      sc.enableBrakeMode( true );

      return sc;
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.subsystems.ISubsystem#reset()
    */
   @Override
   public void reset()
   {
      stop();
   }


   /***********************************
    * State Management Section
    ***********************************/

   /**
    * Sets the module state as to whether the motor is running or not. This also
    * includes an update to the dashboard indicators.
    *
    * @param motor
    **/
   private void setMotorRunning( boolean motor )
   {
      smartDashboard.putBoolean( telemetryStateName, motor );
   }


   /***********************************
    * Public Control Section
    ***********************************/

   /**
    * Stop the lift moving.
    **/
   public void stop()
   {
      // Do not use "stopMotor" method

      setMotorSpeed( 0.0 );
   }


   /**
    * Raise the scissor lift. Negative values, which would normally imply
    * lowering, are normalized to a positive value first - so the scissor
    * mechanism will always go up.
    *
    * Stopping the lift is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void up( double speed )
   {
      speed = ensurePositiveSpeed( speed );

      setMotorSpeed( fixPolarity( speed ) );
   }


   /**
    * Lower the scissor lift. Positive values, which would normally imply
    * lifting, are normalized to a negative value first - so the scissor
    * mechanism will always go down.
    *
    * Stopping the lift is not handled by this method, unless by chance.
    *
    * @param speed
    **/
   public void down( double speed )
   {
      speed = ensureNegativeSpeed( speed );

      setMotorSpeed( fixPolarity( speed ) );
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is up and "-"
    * (negative) is down relative to lift.
    *
    * @param speed
    * @return
    **/
   private double fixPolarity( double speed )
   {
      // This is where you change the sign to fix polarity of lift if necessary
      // Lift is + up and - down
      return speed;
   }


   /**
    * Sets the speed / input to the actual hardware speed controllers for the
    * motor. Includes update to the dashboard.
    *
    * @param speed - setting for speed controller
    **/
   private void setMotorSpeed( double speed )
   {
      smartDashboard.putNumber( telemetrySpeedName, speed );
      setMotorRunning( !isZero( speed ) );

      motor.set( speed );
   }

}
