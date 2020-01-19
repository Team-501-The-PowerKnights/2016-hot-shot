/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.modes.autonomous.EmptyAutonomousMode;
import com.powerknights.frc2016.robot.modes.autonomous.SimpleDriveForwardAutonomousMode;
import com.powerknights.frc2016.robot.modes.teleoperated.RealTeleoperatedMode;
import com.powerknights.frc2016.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.Timer;


/**
 * This is top-level class for the 2016 Robot. The SampleRobot class is the base
 * of a robot application that will automatically call your Autonomous and
 * OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * @author first.stu
 **/
public class Team501Robot
   extends BaseRobot
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( Team501Robot.class.getName() );


   public Team501Robot()
   {
      super( Team501Robot.class.getSimpleName() );
      logger.info( "constructing" );
      logger.info( "constructed" );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.BaseRobot#autonomous()
    */
   @Override
   public void autonomous()
   {
      /*
       * This function is called once each time the robot enters autonomous
       * mode.
       */
      logger.info( "starting autonomous" );

      if ( autonomous == null )
      {
         initAutonomousMode();
      }

      // Reset the state of all the subsystems to known and "off"
      resetSubsystems();

      // Setup
      autonomous.setUp();

      // Do it all
      autonomous.runIt();

      // Clean up
      autonomous.cleanUp();

      logger.info( "stopping autonomous" );
   }


   private void initAutonomousMode()
   {
      logger.info( "starting" );

      if ( prefsManager.runAutonomous() )
      {
         // TODO - Implement a real autonomous mode
         autonomous = new SimpleDriveForwardAutonomousMode( this );
      }
      else
      {
         autonomous = new EmptyAutonomousMode( this );
      }

      logger.info( "initialized" );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.BaseRobot#operatorControl()
    */
   @Override
   public void operatorControl()
   {
      /*
       * This function is called once each time the robot enters operator
       * control.
       */
      logger.info( "starting teleoperated" );

      if ( teleoperated == null )
      {
         initTeleoperatedMode();
      }

      // Reset the state of all the subsystems to known and "off"
      resetSubsystems();

      // Setup
      teleoperated.setUp();

      // Do it all
      teleoperated.runIt();

      // FIXME - Does not stop the threads of controller / gamepads

      // Clean up
      teleoperated.cleanUp();

      logger.info( "stopping teleoperated" );
   }


   private void initTeleoperatedMode()
   {
      logger.info( "starting" );

      // We only have one teleoperated mode - the "real" one
      teleoperated = new RealTeleoperatedMode( this );

      logger.info( "initialized" );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.BaseRobot#test()
    */
   @Override
   public void test()
   {
      logger.info( "starting test" );

      boolean doHome = true;
      while ( isTest() && isEnabled() )
      {
         if ( doHome )
         {
            System.out.println( "homeElevation" );
            Shooter.getInstance().homeElevation();
            doHome = false;
         }
         else
         {
            System.out.println( "setForDistance" );
            Shooter.getInstance().setForDistance( 0.0 );
            doHome = true;
         }
         Timer.delay( 5.0 ); // wait just to break up busy loop
      }

      logger.info( "stopping test" );
   }

}
