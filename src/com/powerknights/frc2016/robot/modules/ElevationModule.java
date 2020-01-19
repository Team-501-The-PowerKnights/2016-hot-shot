/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.modules;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.config.ShooterConfig;
import com.powerknights.frc2016.robot.managers.PreferencesManager;
import com.powerknights.frc2016.robot.sensors.ShooterElevation;
import com.powerknights.frc2016.utils.StringUtils;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SpeedController;


/**
 * @author first.stu
 *         <p>
 *         The gearbox is at 20:1 (20 rotations of the motor is one rotation of
 *         the output shaft).
 *         <p>
 *         -20 is roughly the max value to use for out.
 **/
public class ElevationModule
   extends PWMModule
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( ElevationModule.class.getName() );


   static
   {
      // LOGGER - Override of default level
      // RioLogger.setLevel( logger, Level.TRACE );
   }

   /** Singleton instance of class for all to use **/
   private static ElevationModule ourInstance;
   /** Name of our module **/
   private static final String myName = "ElevationModule";


   /**
    * Constructs instance of the shooter elevation module. Assumed to be called
    * before any usage of the module; and verifies only called once. Allows
    * controlled startup sequencing of the robot and all it's module.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( myName + " Already Constructed" );
      }
      ourInstance = new ElevationModule();
   }


   /**
    * Returns the singleton instance of the shooter elevation module. If it
    * hasn't been constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of scissor lift
    **/
   public static ElevationModule getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( myName + " Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Speed controller for elevation angle **/
   private final SpeedController motor;
   /** SmartDash component name for motor **/
   private final String elevationMotorName = "elevationMotor";

   /** Telemetry state name for motor **/
   private final String elevationMotorTelemetryState = elevationMotorName;
   /** Telemetry speed name for motor **/
   private final String elevationMotorTelemetrySpeed = "elevationSpeed";
   /** Sensor for elevation encoder **/
   @SuppressWarnings( "unused" )
   private final ShooterElevation shooterElevation;

   /** Flag for whether preferences allow auto-elevation process **/
   private final boolean useAutoElevation;
   /** Flag to keep state on auto-elevation process **/
   private boolean autoElevationActive;
   /** Telemetry state name for auto-elevation **/
   private final String autoElevationActiveTelemetryState = "autoElevation";

   /** Current encoder / PID set point for distance to target **/
   private double distanceSetPoint;
   /** **/
   private static final double invalidDistance = -1.0;
   /** Don't adjust for distance if delta is less than this value **/
   private static final double distanceDelta = 0.5;


   private ElevationModule()
   {
      logger.info( "constructing" );

      motor = constructMotor();
      shooterElevation = constructEncoder();

      useAutoElevation = PreferencesManager.getInstance().useAutoElevation();

      disableAutoElevation();

      logger.info( "constructed" );
   }


   /**
    * Constructs/initializes the elevation angle speed controller
    *
    * @return constructed and initialized motor
    */
   private SpeedController constructMotor()
   {
      final int canID = ShooterConfig.getElevationChannel();
      final CANTalon sc = new CANTalon( canID );
      final String component = elevationMotorName + "[" + canID + "]";
      liveWindow.addActuator( myName, component, sc );

      intializePosition( sc );

      initializeManual( sc );

      return sc;
   }


   private void intializePosition( CANTalon sc )
   {
      logger.trace( "initializePosition()" );

      //
      int absolutePosition = sc.getPulseWidthPosition() & 0xFFF;
      sc.setEncPosition( absolutePosition );
      absolutePosition = 0;
      sc.setEncPosition( 0 );
   }


   private void initializeManual( CANTalon sc )
   {
      logger.trace( "initializeManual()" );

      //
      sc.changeControlMode( CANTalon.TalonControlMode.PercentVbus ); // Position
      //
      sc.setFeedbackDevice( CANTalon.FeedbackDevice.CtreMagEncoder_Absolute );
      //
      sc.configNominalOutputVoltage( +0f, -0f );
      sc.configPeakOutputVoltage( 6, -6 ); // 12 is 100%
   }


   private void initializeAutomatic( CANTalon sc )
   {
      logger.trace( "initializeAutomatic()" );

      //
      sc.changeControlMode( CANTalon.TalonControlMode.Position );
      //
      sc.setFeedbackDevice( CANTalon.FeedbackDevice.CtreMagEncoder_Relative );
      //
      sc.configNominalOutputVoltage( +0f, -0f );
      sc.configPeakOutputVoltage( 6, -6 ); // 12 is 100%
      //
      sc.setProfile( 0 );
      sc.setPID( 0.15, 0.0, 0.0 );

      // sc.setAllowableClosedLoopErr( 0 );
      // sc.configEncoderCodesPerRev( 409600 );

      // units are in motor revolutions
      // sc.setForwardSoftLimit( absolutePosition + 0.5 );
      sc.enableForwardSoftLimit( false );
      // sc.setReverseSoftLimit( absolutePosition - 0.5 );
      sc.enableReverseSoftLimit( false );
   }


   /**
    * Constructs/initializes the elevation angle encoder sensor
    *
    * @return constructed and initialized motor
    */
   private ShooterElevation constructEncoder()
   {
      final ShooterElevation elevation = new ShooterElevation( getMotor() );
      return elevation;
   }


   // TODO - Implement as a "real" sensor so comes out of this class
   private CANTalon getMotor()
   {
      return (CANTalon) motor;
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.modules.IModule#reset()
    */
   @Override
   public void reset()
   {
      logger.trace( "reset()" );

      distanceSetPoint = invalidDistance;

      stop();
   }


   /***********************************
    * State Management Section
    ***********************************/

   private void setAutoElevationActive( boolean active )
   {
      smartDashboard.putBoolean( autoElevationActiveTelemetryState, active );
   }


   private void setMotorRunning( boolean motor )
   {
      smartDashboard.putBoolean( elevationMotorTelemetryState, motor );
   }


   /***********************
    * Public Controls
    **********************/

   /**
    * Stops the shooter.
    */
   public void stop()
   {
      logger.trace( "stop()" );

      // Do not use "stopMotor" method

      initializeManual( getMotor() );
      setMotorSpeed( 0.0 );
   }


   /**
    *
    **/
   public void enableAutoElevation()
   {
      logger.debug( "enableAutoElevation()" );
      if ( !useAutoElevation )
      {
         logger.warn( "auto-elevation not enabled in preferences" );
         return;
      }

      setMotorSpeed( 0.0 );

      autoElevationActive = true;
      setAutoElevationActive( autoElevationActive );

      distanceSetPoint = invalidDistance;

      setMotorRunning( true );

      initializeAutomatic( getMotor() );
   }


   /**
    *
    **/
   public void disableAutoElevation()
   {
      logger.debug( "disableAutoElevation()" );
      if ( !useAutoElevation )
      {
         logger.warn( "auto-elevation not enabled in preferences" );
         return;
      }

      autoElevationActive = false;
      setAutoElevationActive( autoElevationActive );

      distanceSetPoint = invalidDistance;

      setMotorRunning( false );

      initializeManual( getMotor() );
   }


   /**
    *
    **/
   public void setHome()
   {
      if ( !autoElevationActive )
      {
         logger.trace( "not active; ignoring setHome()" );
         return;
      }
      logger.debug( "setHome()" );

      getMotor().set( 0.0 );
   }


   /**
    * Sets the elevation based on the distance from the target.
    *
    * @param distance
    **/
   public void setForDistance( double distance )
   {
      if ( !autoElevationActive )
      {
         logger.trace( "not active; ignoring setForDistance()" );
         return;
      }
      logger.trace( "setForDistance() @ {}", distance );

      if ( !isUpdateToDistance( distance ) )
      {
         logger.trace( "already at {} so ignore update", distance );
         return;
      }

      distanceSetPoint = distance;

      final long clicks = convertDistanceToEncoder( distance );
      logger.trace( "active and update required: distance={}, clicks={}",
         StringUtils.format( distance, 2 ), clicks );

      final double revs = convertEncoderToRevolutions( clicks );

      getMotor().set( -15.10 ); // -15 14.5 14.75
   }


   /**
    * Determine whether we should update the elevation based on the target
    * distance, or leave it alone as it's not different enough.
    *
    * @param distance - new distance to target for consideration
    * @return <code>true</code> if and only if greater than the defined delta,
    *         <code>false</code> otherwise
    **/
   private boolean isUpdateToDistance( double distance )
   {
      return ( ( distanceSetPoint < 0.0 )
         || ( Math.abs( distanceSetPoint - distance ) > distanceDelta ) );
   }


   /**
    * @param distance
    * @return
    **/
   private long convertDistanceToEncoder( double distance )
   {
      // From Carter
      final double clicks = ( ( ( 3.84514610 * Math.pow( distance, 3 ) )
         + ( 242.179816 * Math.pow( distance, 2 ) ) )
         + ( -933.87707 * distance ) ) - 4615.47294;
      return Math.round( clicks );
   }


   /**
    * @param clicks
    * @return
    **/
   private double convertEncoderToRevolutions( long clicks )
   {
      // Scale factor is 20 clicks to revolutions
      return -( clicks * 20 ); // negative numbers raise
   }


   /**
    * + raises elevation (lower shot angle), - lowers elevation (higher shot
    * angle)
    *
    * @param speed
    **/
   public void adjust( double speed )
   {
      if ( autoElevationActive )
      {
         logger.trace( "not active; ignoring adjust()" );
         return;
      }

      setMotorSpeed( fixPolarity( speed ) );
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is "up" and "-"
    * (negative) is "down" relative to shooter.
    *
    * @param speed
    * @return
    **/
   private double fixPolarity( double speed )
   {
      // This is where you change the sign to fix polarity if necessary
      // Is + down and - up
      return -speed;
   }


   /**
    * Stop the elevation moving.
    **/
   public void stopElevation()
   {
      // Do not use the "stopMotor" method

      setMotorSpeed( 0.0 );
   }


   /**
    * Sets the speed / inputs to the actual hardware speed controllers for the
    * shooter elevation. Includes updates to the dashboard.
    *
    * @param speed
    **/
   private void setMotorSpeed( double speed )
   {
      logger.trace( "setMotorSpeed = {}", speed );
      smartDashboard.putNumber( elevationMotorTelemetrySpeed, speed );
      setMotorRunning( !isZero( speed ) );
      motor.set( speed );
   }

}
