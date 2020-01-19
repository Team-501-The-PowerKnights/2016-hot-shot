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
import com.powerknights.frc2016.utils.Controller;


/**
 * @author first.stu
 **/
public class HistoryCollector
   extends Controller
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( HistoryCollector.class.getName() );

   /** Singleton instance of class for all to use **/
   private static HistoryCollector ourInstance;

   /** List of all the collectors to collect **/
   private final Vector< IHistoryCollector > collectors;


   private HistoryCollector()
   {
      super( "History Collector", DelayTimeConfig.getHistoryCollectorDelay() );

      collectors = new Vector< IHistoryCollector >();
   }


   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException(
            "HistoryCollector Already Constructed" );
      }
      ourInstance = new HistoryCollector();
   }


   public static HistoryCollector getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException(
            "HistoryCollector Not Constructed Yet" );
      }
      return ourInstance;
   }


   public void addCollector( IHistoryCollector collector )
   {
      collectors.addElement( collector );
   }


   public void removeCollector( IHistoryCollector collector )
   {
      collectors.removeElement( collector );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2013.utils.Controller#doIt()
    */
   @Override
   public boolean doIt()
   {
      for ( final IHistoryCollector collector : collectors )
      {
         try
         {
            collector.collect();
         }
         catch ( final Exception ex )
         {
            logger.error( "IHistoryCollector failed; removing from list" );
            collectors.removeElement( collector );
            break;
         }
      }

      // Keep on going ...
      return false;
   }

}
