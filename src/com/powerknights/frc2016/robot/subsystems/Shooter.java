/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.subsystems;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.modules.ElevationModule;
import com.powerknights.frc2016.robot.modules.FeederModule;
import com.powerknights.frc2016.robot.modules.FeederPosition;
import com.powerknights.frc2016.robot.modules.ShooterModule;


/**
 * @author first.stu
 **/
public class Shooter
   extends PWMSubsystem
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( Shooter.class.getName() );

   /** Singleton instance of class for all to use **/
   private static Shooter ourInstance;
   /** Name of our subsystem **/
   @SuppressWarnings( "unused" )
   private static final String myName = "Shooter";


   /**
    * Constructs instance of the shooter subsystem. Assumed to be called before
    * any usage of the subsystem; and verifies only called once. Allows
    * controlled startup sequencing of the robot and all it's subsystems.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( "Shooter Already Constructed" );
      }
      ourInstance = new Shooter();
   }


   /**
    * Returns the singleton instance of the shooter subsystem. If it hasn't been
    * constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of drive train
    **/
   public static Shooter getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( "Shooter Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Feeder module for motor and PID control **/
   private final FeederModule feederModule;
   /** Shooter module for motors driving wheels **/
   private final ShooterModule shooterModule;
   /** Elevation angle module for motor and PID control **/
   private final ElevationModule elevationModule;


   /** Telemetry state name for motor **/

   private Shooter()
   {
      logger.info( "constructing" );

      FeederModule.constructInstance();
      feederModule = FeederModule.getInstance();

      ShooterModule.constructInstance();
      shooterModule = ShooterModule.getInstance();

      ElevationModule.constructInstance();
      elevationModule = ElevationModule.getInstance();

      logger.info( "constructed" );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.subsystems.ISubsystem#reset()
    */
   @Override
   public void reset()
   {
      feederModule.reset();
      shooterModule.reset();
      elevationModule.reset();

      stop();
   }

   /***********************************
    * State Management Section
    ***********************************/


   /***********************
    * Public Controls
    **********************/

   /**
    * Stops the shooter.
    */
   public void stop()
   {
      // Do not use "stopMotor" method

      feederModule.stop();
      shooterModule.stop();
      elevationModule.stop();
   }


   /***********************
    * Actual Shooter
    **********************/

   public void shoot( double speed )
   {
      shooterModule.shoot( speed );
   }


   public void eject( double speed )
   {
      shooterModule.eject( speed );
   }


   /***********************
    * Feeder
    **********************/

   /**
    * Enables the PID-based rotation mode of the feeder; which will use the
    * button inputs from the Operator controller to determine its position.
    **/
   public void enablePIDFeederPosition()
   {
      feederModule.enablePIDRotation();
   }


   /**
    * Disables the PID-based rotation mode of the feeder. Control of the adjust
    * returns to 'manual' inputs.
    **/
   public void disablePIDFeederPosition()
   {
      feederModule.disablePIDRotation();
   }


   /**
    * Sets the feeder position to one of predefined set points.
    *
    * @param position set point
    **/
   public void setFeederPosition( FeederPosition position )
   {
      feederModule.setPosition( position );
   }


   /**
    * + feeds ball to shoot, - unloads ball
    *
    * @param speed
    **/
   public void rotateFeeder( double speed )
   {
      feederModule.adjust( speed );
   }


   public void stopFeeder()
   {
      feederModule.stop();
   }


   /***********************
    * Elevation
    **********************/

   /**
    * Enables the auto-elevation mode of the shooter; which will use the
    * distance from the tower target to determine what the elevation should be
    * for shooting.
    **/
   public void enableAutoElevation()
   {
      elevationModule.enableAutoElevation();
   }


   /**
    * Disables the auto-elevation mode of the shooter. Control of the adjust
    * returns to 'manual' inputs.
    **/
   public void disableAutoElevation()
   {
      elevationModule.disableAutoElevation();
   }


   /**
    * Sets the position of the elevation to the 'home' location (at the bottom
    * of the travel distance).
    */
   public void homeElevation()
   {
      elevationModule.setHome();
   }


   /**
    * Sets the elevation based on the distance from the target.
    *
    * @param distance
    **/
   public void setForDistance( double distance )
   {
      elevationModule.setForDistance( distance );
   }


   /**
    * + raises elevation (lower shot angle), - lowers elevation (higher shot
    * angle)
    *
    * @param speed
    **/
   public void adjustElevation( double speed )
   {
      elevationModule.adjust( speed );
   }

}
