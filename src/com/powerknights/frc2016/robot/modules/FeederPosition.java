/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.modules;


/**
 * @author first.stu
 * @author first.adam
 **/
public enum FeederPosition
{

   UNSET( -1.0 ),
   UP( 0.0 ),
   UP_FRONT( 0.125 ),
   FRONT( 0.250 ),
   DOWN_FRONT( 0.375 ),
   // DOWN( 0.500 ),
   DOWN( 0.600 ),
   DOWN_REAR( 0.625 ),
   REAR( 0.750 ),
   UP_REAR( 0.875 );

   /** number of teeth on the output sprocket **/
   static private final double GEARBOX_SHAFT_SPROCKET_TEETH = 22;
   /** number of teeth on the drive sprocket **/
   static private final double FLAP_SHAFT_SPROCKET_TEETH = 50;
   /** Ratio of Gearbox Ex. 100:1 is 100 **/
   static private final double GEARBOX_RATIO = 100;

   private final double computedSprocketRatio;
   @SuppressWarnings( "unused" )
   private final double motorRevsPerFlapRev;

   private double rotations;


   private FeederPosition( double rotations )
   {
      this.rotations = rotations;

      computedSprocketRatio =
         FLAP_SHAFT_SPROCKET_TEETH / GEARBOX_SHAFT_SPROCKET_TEETH;
      motorRevsPerFlapRev = computedSprocketRatio * GEARBOX_RATIO;
   }


   public double getPosition()
   {
      final double motorRevsPerFlapRev = 163.6;
      return rotations * motorRevsPerFlapRev;
   }

}
