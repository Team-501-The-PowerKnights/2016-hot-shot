/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.modes.teleoperated;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.controllers.DriverController;
import com.powerknights.frc2016.utils.Controller;

import edu.wpi.first.wpilibj.RobotBase;


/**
 * first.stu
 **/
public class SimpleDriveTeleoperatedMode
   extends TeleoperatedMode
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( SimpleDriveTeleoperatedMode.class.getName() );


   public SimpleDriveTeleoperatedMode( RobotBase robot )
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
      logger.info( "Teloperated with simple drive only" );
      lcdManager.logMessage( "Teloperated - Drive" );

      /*
       * Create the controllers (one for driver only)
       */
      final Controller driverController = new DriverController();

      /*
       * Start the controllers
       */
      driverController.start();

      /*
       * Something just to hold us here so the threads keep running ...
       */
      while ( isActive() )
      {
         sleep( 250 );
      }

      /*
       * Stop the controllers
       */
      driverController.quit();

      logger.info( "finished" );
   }

}
