/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.sensors;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;

import edu.wpi.first.wpilibj.CANTalon;


/**
 * @author first.stu
 **/
public class ShooterElevation
   extends Sensor< Long >
{

   /** Our classes' logger **/
   @SuppressWarnings( "unused" )
   private static final Logger logger =
      RioLogger.getLogger( ShooterElevation.class.getName() );

   /** The sensor we are wrapping (specific type) **/
   private final CANTalon sensor;


   /**
    * @param sensor
    **/
   public ShooterElevation( CANTalon encoder )
   {
      super( encoder );
      // Now that it's created in superclass, typecast locally
      sensor = (CANTalon) sendable;
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.sensors.Sensor#reset()
    */
   @Override
   public void reset()
   {
      System.out.println( "***** reset() in ShooterElevation" );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.sensors.Sensor#get()
    */
   @Override
   public Long get()
   {
      final long position = sensor.getEncPosition();
      return position;
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.controllers.ISensorUpdater#update()
    */
   @Override
   public void update()
   {
      smartDashboard.putNumber( "elevationPosition", get() );
   }

}
