/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
 **/

package com.powerknights.frc2016.robot.controllers;


import java.util.Vector;

import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.config.DelayTimeConfig;
import com.powerknights.frc2016.robot.managers.PreferencesManager;
import com.powerknights.frc2016.utils.Controller;


/**
 * @author first.stu
 **/
public class SensorUpdater
   extends Controller
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( SensorUpdater.class.getName() );

   /** Singleton instance of class for all to use **/
   private static SensorUpdater ourInstance;

   /** List of all the sensors to update **/
   private final Vector< ISensorUpdater > sensors;

   /** Whether to update on thread or not **/
   private final boolean runThread;


   private SensorUpdater()
   {
      super( "Sensor Updater", DelayTimeConfig.getSensorUpaterDelay() );

      sensors = new Vector< ISensorUpdater >();

      runThread = PreferencesManager.getInstance().runSensorsThread();
   }


   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( "SensorUpdater Already Constructed" );
      }
      ourInstance = new SensorUpdater();
   }


   public static SensorUpdater getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( "SensorUpdater Not Constructed Yet" );
      }
      return ourInstance;
   }


   public void addSensor( ISensorUpdater sensor )
   {
      sensors.addElement( sensor );
   }


   public void removeSensor( ISensorUpdater sensor )
   {
      sensors.removeElement( sensor );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2013.utils.Controller#doIt()
    */
   @Override
   public boolean doIt()
   {
      if ( !runThread )
      {
         return true;
      }

      for ( final ISensorUpdater sensor : sensors )
      {
         try
         {
            sensor.update();
         }
         catch ( final Exception ex )
         {
            logger.error( "ISensorUpdater failed; removing from list" );
            sensors.removeElement( sensor );
            break;
         }
      }

      // Keep on going ...
      return false;
   }

}
