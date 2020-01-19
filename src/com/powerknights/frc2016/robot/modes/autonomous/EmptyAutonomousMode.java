/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
 **/

package com.powerknights.frc2016.robot.modes.autonomous;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;


/**
 * @author first.stu
 **/
public class EmptyAutonomousMode
   extends AutonomousMode
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( EmptyAutonomousMode.class.getName() );


   public EmptyAutonomousMode( RobotBase robot )
   {
      super( robot );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2013.modes.ModeImplementer#runIt()
    */
   @Override
   public void runIt()
   {
      logger.info( "Autonomous with busy wait" );
      lcdManager.logMessage( "Autonomous - Empty" );

      // Something just to hold us here so the threads keep running ...
      while ( isActive() )
      {
         Timer.delay( 0.5 );
      }

      logger.info( "finished" );
   }

}
