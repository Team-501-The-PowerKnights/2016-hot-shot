/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.vision;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;


/**
 * @author first.stu
 **/
public class VisionUpdate
{

   /** Our classes' logger **/
   @SuppressWarnings( "unused" )
   private static final Logger logger =
      RioLogger.getLogger( VisionUpdate.class.getName() );

   /** Valid update flag **/
   private boolean valid;
   /** (Offset) angle to target **/
   public double angle; // degrees
   /** Distance to target **/
   public double distance; // feet


   public VisionUpdate()
   {
      setValid( false );
   }


   void setValid( boolean valid )
   {
      this.valid = valid;
   }


   public boolean isValid()
   {
      return valid;
   }

}
