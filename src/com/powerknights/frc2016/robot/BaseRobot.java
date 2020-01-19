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
import com.powerknights.frc2016.robot.config.SubsystemsConfig;
import com.powerknights.frc2016.robot.controllers.HistoryCollector;
import com.powerknights.frc2016.robot.controllers.MatchTimeUpdater;
import com.powerknights.frc2016.robot.controllers.SensorUpdater;
import com.powerknights.frc2016.robot.managers.DashboardManager;
import com.powerknights.frc2016.robot.managers.DriverStationManager;
import com.powerknights.frc2016.robot.managers.LCDManager;
import com.powerknights.frc2016.robot.managers.LiveWindowManager;
import com.powerknights.frc2016.robot.managers.PreferencesManager;
import com.powerknights.frc2016.robot.managers.RobotManager;
import com.powerknights.frc2016.robot.managers.SmartDashboardManager;
import com.powerknights.frc2016.robot.modes.ModeImplementer;
import com.powerknights.frc2016.robot.subsystems.Chassis;
import com.powerknights.frc2016.robot.subsystems.DriveTrain;
import com.powerknights.frc2016.robot.subsystems.HMIControllers;
import com.powerknights.frc2016.robot.subsystems.Lifter;
import com.powerknights.frc2016.robot.subsystems.Shooter;
import com.powerknights.frc2016.robot.vision.NetworkTablesVision;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;


/**
 * @author first.stu
 **/
abstract class BaseRobot
   extends SampleRobot
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( BaseRobot.class.getName() );

   /** Name of our robot **/
   protected final String myName;

   /** Handle to the driver station on operator console (laptop) **/
   protected DriverStationManager dsManager;
   /** Handle to the LCD on the driver station **/
   protected LCDManager lcdManager;
   /** Handle to smart dashboard **/
   protected SmartDashboardManager smartDashboard;
   /** Handle to Preferences Manager **/
   protected PreferencesManager prefsManager;

   /** Implementation of autonomous mode **/
   protected ModeImplementer autonomous;
   /** Implementation of teleoperated mode **/
   protected ModeImplementer teleoperated;


   /**
    * Creates an instance of the base robot, and holds continued startup and
    * execution until <i>Driver Station</i> data arrives, so that the run-time
    * configuration is available.
    *
    * @param robotName - name of the robot (allows more generic code)
    **/
   protected BaseRobot( String robotName )
   {
      logger.info( "constructing" );

      // Get class name for more generic code
      myName = robotName;

      // Wait until we get the configuration data from driver station
      waitForDriverStationData();

      logger.info( "constructed" );
   }


   /**
    * Holds the constructor until we receive at least one update of the control
    * data, which holds the run-time configuration.
    **/
   private void waitForDriverStationData()
   {
      long count = 0;
      while ( !DriverStation.getInstance().isNewControlData() )
      {
         if ( ( count % 100 ) == 0 )
         {
            logger.debug( "Waiting ..." );
         }
         try
         {
            Thread.sleep( 100 );
         }
         catch ( final InterruptedException ex )
         {
            // We'll ignore it
         }
         count++;
      }
   }


   /*
    * (non-Javadoc)
    *
    * @see edu.wpi.first.wpilibj.SampleRobot#robotInit()
    */
   @Override
   protected void robotInit()
   {
      logger.info( "initializing" );

      /*
       * Create handles to managers and external systems
       */
      constructManagers();

      /*
       * Create handles to operator console and initialize
       */
      initConsoleInterface();
      /*
       * Note we are initializing
       */
      lcdManager.setMode( "Init" );
      smartDashboard.putString( "MODE", "Init" );

      /*
       * Display some configuration info
       */
      lcdManager.setConfig( myName );

      /*
       * Create Vision server and start
       */
      constructAndStartVision();

      /*
      *
      */
      // Could do sensors outside of hardware / subsystems
      constructControllers();

      /*
       * Create handles to framework and hardware for robot and initialize
       */
      initSubsystems();

      /*
       * Create handles to devices for driver and operator and initialize
       */
      initOperatorControls();

      /*
       *
       */
      // Could do sensors outside of hardware / subsystems
      startControllers();

      /*
       * Note we are "booted"
       */
      lcdManager.setMode( "Booted" );
      smartDashboard.putString( "MODE", "Booted" );
      /*
       * Note we have no timeouts to start
       */
      smartDashboard.putTimeout( false, "" );

      logger.info( "initialized" );
   }


   private void constructManagers()
   {
      logger.info( "starting" );

      // Robot itself (so we don't have to pass ourself around)
      RobotManager.constructInstance( this );

      // Preferences Manager on Robot (using wpilib-preferences.ini file)
      PreferencesManager.constructInstance();

      // National Instruments Dashboard on Laptop
      DashboardManager.constructInstance();

      // Driver Station on Laptop
      DriverStationManager.constructInstance();

      // LCD Display on Driver Station
      LCDManager.constructInstance();

      // Smart Dashboard on Laptop
      SmartDashboardManager.constructInstance();

      // Live Window on Laptop
      LiveWindowManager.constructInstance();

      logger.info( "constructed" );
   }


   private void initConsoleInterface()
   {
      logger.info( "starting" );

      // Driver Station
      dsManager = DriverStationManager.getInstance();

      // LCD on Driver Station
      lcdManager = LCDManager.getInstance();
      lcdManager.setBuild( CodeVersionInfo.version );

      // SmartDashboard from WPILib
      smartDashboard = SmartDashboardManager.getInstance();

      // Preferences from file on robot wpilib-preferences.ini
      prefsManager = PreferencesManager.getInstance();

      logger.info( "initialized" );
   }


   protected void constructAndStartVision()
   {
      logger.info( "entering" );

      if ( PreferencesManager.getInstance().runVision() )
      {
         lcdManager.appendSubsystem( 'V' );
         smartDashboard.putBoolean( "Vision", true );
         if ( PreferencesManager.getInstance().useNetworkTablesVision() )
         {
            logger.info( "running vision and using NetworkTables" );
            NetworkTablesVision.constructInstance();
         }
      }
      else
      {
         lcdManager.appendSubsystem( 'v' );
         smartDashboard.putBoolean( "Vision", false );
      }

      logger.info( "exiting" );
   }


   protected void constructControllers()
   {
      logger.info( "starting" );

      // Dashboard Updater
      // TODO - Implement the NI dashboard data parser
      // DashboardUpdater.constructInstance( );

      // MatchTime Updater
      MatchTimeUpdater.constructInstance();

      // Sensors
      SensorUpdater.constructInstance();

      // History
      HistoryCollector.constructInstance();

      logger.info( "constructed" );
   }


   protected void initSubsystems()
   {
      logger.info( "starting" );

      // Top level status
      smartDashboard.putBoolean( "SUBSYSTEMS", false );
      boolean overall = true;

      // Drive
      if ( SubsystemsConfig.enableDriveTrain() )
      {
         DriveTrain.constructInstance();
         DriveTrain.getInstance().reset();
         lcdManager.appendSubsystem( 'D' );
         smartDashboard.putBoolean( "Drive", true );
      }
      else
      {
         lcdManager.appendSubsystem( 'd' );
         smartDashboard.putBoolean( "Drive", false );
         overall = false;
      }

      // Chassis
      if ( SubsystemsConfig.enableChassis() )
      {
         Chassis.constructInstance();
         Chassis.getInstance().reset();
         lcdManager.appendSubsystem( 'C' );
         smartDashboard.putBoolean( "Chassis", true );
      }
      else
      {
         lcdManager.appendSubsystem( 'c' );
         smartDashboard.putBoolean( "Chassis", false );
         overall = false;
      }

      // Shooter
      if ( SubsystemsConfig.enableShooter() )
      {
         Shooter.constructInstance();
         Shooter.getInstance().reset();
         lcdManager.appendSubsystem( 'S' );
         smartDashboard.putBoolean( "Shooter", true );
      }
      else
      {
         lcdManager.appendSubsystem( 's' );
         smartDashboard.putBoolean( "Shooter", false );
      }

      // Lifter
      if ( SubsystemsConfig.enableShooter() )
      {
         Lifter.constructInstance();
         Lifter.getInstance().reset();
         lcdManager.appendSubsystem( 'L' );
         smartDashboard.putBoolean( "Lifter", true );
      }
      else
      {
         lcdManager.appendSubsystem( 'l' );
         smartDashboard.putBoolean( "Lifter", false );
      }

      // Top level status
      smartDashboard.putBoolean( "SUBSYSTEMS", overall );

      logger.info( "initialized" );
   }


   protected void resetSubsystems()
   {
      logger.info( "starting" );

      // Chassis
      if ( SubsystemsConfig.enableChassis() )
      {
         Chassis.getInstance().reset();
      }

      // Drive
      if ( SubsystemsConfig.enableDriveTrain() )
      {
         DriveTrain.getInstance().reset();
      }

      // Shooter
      if ( SubsystemsConfig.enableShooter() )
      {
         Shooter.getInstance().reset();
      }

      // Lifter
      if ( SubsystemsConfig.enableLifter() )
      {
         Lifter.getInstance().reset();
      }

      logger.info( "reset" );
   }


   protected void initOperatorControls()
   {
      logger.info( "starting" );

      // HMI Controls
      HMIControllers.construct();
      lcdManager.appendSubsystem( 'H' );
      smartDashboard.putBoolean( "HMI", true );

      logger.info( "initialized" );
   }


   protected void startControllers()
   {
      logger.info( "starting" );

      // Dashboard Updater
      // DashboardUpdater.getInstance( ).start( );

      // MatchTime Updater
      MatchTimeUpdater.getInstance().start();

      // Sensors
      SensorUpdater.getInstance().start();

      // History
      HistoryCollector.getInstance().start();

      logger.info( "started" );
   }


   /*
    * (non-Javadoc)
    *
    * @see edu.wpi.first.wpilibj.SampleRobot#disabled()
    */
   @Override
   protected void disabled()
   {
      logger.info( "entering" );

      // Reset the driver station GUI
      lcdManager.reset();

      // Ensure all the subsystems are shut down
      resetSubsystems();

      logger.info( "exiting" );
   }


   /*
    * (non-Javadoc)
    *
    * @see edu.wpi.first.wpilibj.SampleRobot#autonomous()
    */
   @Override
   public void autonomous()
   {
      logger.info( "starting" );

      while ( isAutonomous() && isEnabled() )
      {
         Timer.delay( 0.1 ); // wait just to break up busy loop
      }

      logger.info( "stopping" );
   }


   /*
    * (non-Javadoc)
    *
    * @see edu.wpi.first.wpilibj.SampleRobot#operatorControl()
    */
   @Override
   public void operatorControl()
   {
      logger.info( "starting" );

      while ( isOperatorControl() && isEnabled() )
      {
         Timer.delay( 0.1 ); // wait just to break up busy loop
      }

      logger.info( "stopping" );
   }


   /*
    * (non-Javadoc)
    *
    * @see edu.wpi.first.wpilibj.SampleRobot#test()
    */
   @Override
   public void test()
   {
      logger.info( "starting" );

      while ( isTest() && isEnabled() )
      {
         Timer.delay( 0.1 ); // wait just to break up busy loop
      }

      logger.info( "stopping" );
   }

}
