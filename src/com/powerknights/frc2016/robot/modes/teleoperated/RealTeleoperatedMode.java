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
import com.powerknights.frc2016.robot.controllers.OperatorController;
import com.powerknights.frc2016.utils.Controller;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;


/**
 * @author first.stu
 **/
public class RealTeleoperatedMode
   extends TeleoperatedMode
{

   /** Our classes' logger **/
   @SuppressWarnings( "unused" )
   private static final Logger logger =
      RioLogger.getLogger( RealTeleoperatedMode.class.getName() );


   public RealTeleoperatedMode( RobotBase robot )
   {
      super( robot );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.modes.ModeImplementer#runIt()
    */
   @Override
   public void runIt()
   {
      final Controller driverController = new DriverController();
      final Controller operatorController = new OperatorController();

      /*
       * Start the controllers / enable them
       */
      driverController.start();
      operatorController.start();

      /*
       * Temporary loop to allow everything in the thread to continue
       */
      while ( isActive() )
      {
         Timer.delay( 250 );
      }

      driverController.quit();
      operatorController.quit();
   }

}
