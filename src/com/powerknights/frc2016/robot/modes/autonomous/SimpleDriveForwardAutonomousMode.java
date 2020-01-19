/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.modes.autonomous;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.subsystems.Chassis;
import com.powerknights.frc2016.robot.subsystems.DriveTrain;
import com.powerknights.frc2016.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;


/**
 * @author first.stu
 **/
public class SimpleDriveForwardAutonomousMode
   extends AutonomousMode
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( SimpleDriveForwardAutonomousMode.class.getName() );

   /** **/
   @SuppressWarnings( "unused" )
   private final Chassis chassis;
   /** **/
   private final DriveTrain drive;


   public SimpleDriveForwardAutonomousMode( RobotBase robot )
   {
      super( robot );

      chassis = Chassis.getInstance();
      drive = DriveTrain.getInstance();
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2013.modes.ModeImplementer#runIt()
    */
   @Override
   public void runIt()
   {
      logger.info( "Autonomous Simple Drive Forward" );
      lcdManager.logMessage( "Autonomous - SDF" );

      // Pause to let other robot get ahead of us
      Timer.delay( 9.0 );

      // FIXME - Move flap up to parked position before starting to drive
      // Shooter.getInstance().enablePIDFeederPosition();
      // Shooter.getInstance().setFeederPosition( FeederPosition.DOWN_REAR );
      // Timer.delay( 0.5 );
      Shooter.getInstance().rotateFeeder( -0.6 );
      Timer.delay( 0.75 );
      Shooter.getInstance().rotateFeeder( 0.0 );

      final double speed = 0.85;
      final double rotation = 0.0;
      drive.arcadeDrive( speed, rotation );

      // Time to get over the obstacle
      Timer.delay( 2.2 );

      // // Drive slower now to approach the wall
      // speed = 0.4;
      // rotation = 0.0;
      // drive.arcadeDrive( speed, rotation );
      //
      // // Now wait until we are at range from the wall
      // long count = 0;
      // while ( isActive() && !chassis.inRange() && ( count < 150 ) )
      // {
      // Timer.delay( 0.02 );
      // count++;
      // }
      //
      // if ( isActive() )
      // {
      // speed = 0.2;
      // rotation = -0.4;
      // drive.arcadeDrive( speed, rotation );
      //
      // Timer.delay( 1.5 );
      // }

      drive.stop();

      // Shooter.getInstance().disablePIDFeederPosition();

      // Something just to hold us here so the threads keep running ...
      // TODO - Get rid of this wait loop?
      while ( isActive() )
      {
         Timer.delay( 0.1 );
      }

      logger.info( "finished" );
   }

}
