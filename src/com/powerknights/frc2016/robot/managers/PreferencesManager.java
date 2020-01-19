/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.managers;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;

import edu.wpi.first.wpilibj.Preferences;


/**
 * @author first.stu
 **/
public class PreferencesManager
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( PreferencesManager.class.getName() );

   /** Singleton instance of class for all to use **/
   private static PreferencesManager ourInstance;


   /**
    * Constructs the singleton instance of the Preferences manager. Assumed to
    * be called before any use of the manager; and verifies only called once.
    *
    * @throws IllegalStateException
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException(
            "PreferencesManager Already Constructed" );
      }
      ourInstance = new PreferencesManager();
   }


   /**
    * Returns the singleton instance of the Preferences manager. Verifies the
    * manager has been successfully created prior to use.
    *
    * @return singleton instance of Preferences manager
    * @throws IllegalStateException
    **/
   public static PreferencesManager getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException(
            "PreferencesManager Not Constructed Yet" );
      }
      return ourInstance;
   }


   private PreferencesManager()
   {
      logger.info( "constructing" );
      logger.info( "constructed" );
   }


   /**
    * Using independent stu drive mode (true) or WPI (false) *
    *
    * @return
    **/
   public boolean useStuDriveMode()
   {
      final String key = "useStuDriveMode";
      return getBoolean( key, true );
   }


   /**
    * Whether to flatten Driver axes inputs (true) or not (false)
    *
    * @return
    **/
   public boolean doFlattenDriverInputResponse()
   {
      final String key = "doFlattenDriverInput";
      return getBoolean( key, false );
   }


   /**
    * Whether to flatten Operator axes inputs (true) or not (false)
    *
    * @return
    **/
   public boolean doFlattenOperatorInputResponse()
   {
      final String key = "doFlattenOperatorInput";
      return getBoolean( key, false );
   }


   /**
    * Whether to do anything in autonomous mode
    *
    * @return
    **/
   public boolean runAutonomous()
   {
      final String key = "runAutonomous";
      return getBoolean( key, false );
   }


   /**
    * Whether to use PIDs to hold subsystem components *
    *
    * @return
    **/
   public boolean usePIDHolds()
   {
      final String key = "usePIDHolds";
      return getBoolean( key, false );
   }


   /**
    * Whether to run Vision for aiming support (true) or not (false)
    *
    * @return
    **/
   public boolean runVision()
   {
      final String key = "runVision";
      return getBoolean( key, false );
   }


   /**
    * Whether to use NetworkTables for vision source (true) or not (false)
    *
    * @return
    **/
   public boolean useNetworkTablesVision()
   {
      final String key = "useNetworkTablesVision";
      return getBoolean( key, false );
   }


   /**
    * Whether to display / use SmartDashboard (true) or not (false)
    *
    * @return
    **/
   public boolean useSmartDashboard()
   {
      final String key = "useSmartDashboard";
      return getBoolean( key, false );
   }


   /**
    * Whether to display / use LiveWindow (true) or not (false)
    *
    * @return
    **/
   public boolean useLiveWindow()
   {
      final String key = "useLiveWindow";
      return getBoolean( key, false );
   }


   /**
    * Whether to run auto-elevation on shooting (true) or not (false)
    *
    * @return
    **/
   public boolean useAutoElevation()
   {
      final String key = "useAutoElevation";
      return getBoolean( key, false );
   }


   /**
    * Whether to enable PID-based rotation position on shooter feeder (true) or
    * not (false)
    *
    * @return
    **/
   public boolean usePIDFeederRotation()
   {
      final String key = "usePIDFeederRotation";
      return getBoolean( key, false );
   }


   /**
    * What the name of the IP-based camera (Axis model) is on the roboRIO that
    * is to be used for the vision processing stuff on targetting.
    * <p>
    * This can be found through the web interface to the roboRIO.
    *
    * @return name of the camera device
    **/
   public String getIpVisionCamera()
   {
      final String key = "ipVisionCamera";
      return getString( key, "cam1" );
   }


   /**
    * Whether to run the dashboard camera source on the robot (true) or not
    * (false)
    *
    * @return
    **/
   public boolean runDashCamera()
   {
      final String key = "runDashCamera";
      return getBoolean( key, false );
   }


   /**
    * What the name of the USB-based camera (Microsoft HD model) is on the
    * roboRIO that is to be used for the generic dashboard camera.
    * <p>
    * This can be found through the web interface to the roboRIO.
    *
    * @return name of the camera device
    **/
   public String getUsbDashCamera()
   {
      final String key = "usbDashCamera";
      return getString( key, "cam2" );
   }


   /**
    * What model the Driver controller is
    *
    * @return
    **/
   public String getDriverController()
   {
      final String key = "driverController";
      return getString( key, "Xbox360Gamepad" );
   }


   /**
    * What model the Operator controller is
    *
    * @return
    **/
   public String getOperatorController()
   {
      final String key = "operatorController";
      return getString( key, "Xbox360Gamepad" );
   }


   /**
    * Whether to run the sensors thread for dashboard updates (true) or not
    * (false).
    * <p>
    * This allows better debugging with the CAN bus missing on different robots.
    *
    * @return
    **/
   public boolean runSensorsThread()
   {
      final String key = "runSensorsThread";
      return getBoolean( key, true );
   }


   /**
    * Whether to run thread timing prints (true) or not (false)
    *
    * @return
    **/
   public boolean runThreadTiming()
   {
      final String key = "runThreadTiming";
      return getBoolean( key, false );
   }


   private boolean getBoolean( String key, boolean ifMissing )
   {
      final Preferences prefs = Preferences.getInstance();
      if ( !prefs.containsKey( key ) )
      {
         logger.error( "Error: Preference not found: " + key );
      }
      return prefs.getBoolean( key, ifMissing );
   }


   @SuppressWarnings( "unused" )
   private int getInt( String key, int ifMissing )
   {
      final Preferences prefs = Preferences.getInstance();
      if ( !prefs.containsKey( key ) )
      {
         logger.error( "Error: Preference not found: " + key );
      }
      return prefs.getInt( key, ifMissing );
   }


   @SuppressWarnings( "unused" )
   private double getDouble( String key, double ifMissing )
   {
      final Preferences prefs = Preferences.getInstance();
      if ( !prefs.containsKey( key ) )
      {
         logger.error( "Error: Preference not found: " + key );
      }
      return prefs.getDouble( key, ifMissing );
   }


   private String getString( String key, String ifMissing )
   {
      final Preferences prefs = Preferences.getInstance();
      if ( !prefs.containsKey( key ) )
      {
         logger.error( "Error: Preference not found: " + key );
      }
      return prefs.getString( key, ifMissing );
   }

   // TEMPORARY CODE FOR VISION!
   // public double getAutoTurnSpeedMin()
   // {
   // return getDouble( "autoTurnSpeedMin", 0.0 );
   // }
   //
   //
   // public double getAutoTurnSpeedMax()
   // {
   // return getDouble( "autoTurnSpeedMax", 0.0 );
   // }
   //
   //
   // public double getAutoZeroVal()
   // {
   // return getDouble( "autoZeroVal", 0.0 );
   // }
   //
   //
   // public double getAutoTimeDelay()
   // {
   // return getDouble( "autoTimeDelay", 0.0 );
   // }

}
