/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
 **/

package com.powerknights.frc2016.robot.managers;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;

import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;


/**
 * @author first.stu
 **/
public class LiveWindowManager
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( LiveWindowManager.class.getName() );

   /** Singleton instance of class for all to use **/
   private static LiveWindowManager ourInstance;


   /**
    * Constructs the singleton instance of the LiveWindow manager. Assumed to be
    * called before any use of the manager; and verifies only called once.
    *
    * @throws IllegalStateException
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException(
            "LiveWindowManager Already Constructed" );
      }
      ourInstance = new LiveWindowManager();
   }


   /**
    * Returns the singleton instance of the LiveWindow manager. Verifies the
    * manager has been successfully created prior to use.
    *
    * @return singleton instance of LiveWindow manager
    * @throws IllegalStateException
    **/
   public static LiveWindowManager getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException(
            "LiveWindowManager Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Cached flag for whether to use Live Window **/
   private final boolean enableLiveWindow;


   private LiveWindowManager()
   {
      logger.info( "constructing" );

      enableLiveWindow = PreferencesManager.getInstance().useLiveWindow();

      logger.info( "constructing" );
   }


   public void addActuator( String subsystem, String name,
      LiveWindowSendable component )
   {
      if ( enableLiveWindow )
      {
         LiveWindow.addActuator( subsystem, name, component );
      }
   }


   public void addSensor( String subsystem, String name,
      LiveWindowSendable component )
   {
      if ( enableLiveWindow )
      {
         LiveWindow.addSensor( subsystem, name, component );
      }
   }

}
