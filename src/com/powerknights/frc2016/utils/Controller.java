/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
 **/

package com.powerknights.frc2016.utils;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.managers.SmartDashboardManager;


/**
 * @author first.stu
 */
public abstract class Controller
   extends LoggedStoppableThread
{

   /** Our classes' logger **/
   @SuppressWarnings( "unused" )
   private static final Logger logger =
      RioLogger.getLogger( Controller.class.getName() );


   /**
    * @param name - name of the controller
    * @param loopDelay - how long between iterations (msec)
    **/
   public Controller( String name, long loopDelay )
   {
      super( name, loopDelay );
   }


   public void setConfiguration( String position, String config )
   {
      SmartDashboardManager.getInstance().putString( position, config );
   }

}