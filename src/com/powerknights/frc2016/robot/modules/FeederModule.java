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
import com.powerknights.frc2016.robot.sensors.FeederRotation;
import com.powerknights.frc2016.utils.StringUtils;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SpeedController;


/**
 * @author first.stu
 * @author first.adam
 **/
public class FeederModule
   extends PWMModule
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( FeederModule.class.getName() );


   static
   {
      // LOGGER - Override of default level
      // RioLogger.setLevel( logger, Level.DEBUG );
   }

   /** Singleton instance of class for all to use **/
   private static FeederModule ourInstance;
   /** Name of our module **/
   private static final String myName = "FeederModule";


   /**
    * Constructs instance of the shooter feeder module. Assumed to be called
    * before any usage of the module; and verifies only called once. Allows
    * controlled startup sequencing of the robot and all it's module.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( myName + " Already Constructed" );
      }
      ourInstance = new FeederModule();
   }


   /**
    * Returns the singleton instance of the shooter feeder module. If it hasn't
    * been constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of scissor lift
    **/
   public static FeederModule getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( myName + " Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Scale factor for speed of feeder **/
   private static final double feederScaleFactor = 0.35;

   /** Speed controller for feeder **/
   private final SpeedController motor;
   /** SmartDash component name for motor **/
   private final String feederMotorName = "feederMotor";

   /** Telemetry state name for motor **/
   private final String feederMotorTelemetryState = feederMotorName;
   /** Telemetry speed name for motor **/
   private final String feederMotorTelemetrySpeed = "feederSpeed";
   /** Sensor for rotation encoder **/
   @SuppressWarnings( "unused" )
   private final FeederRotation feederRotation;

   /** Flag for whether preferences allow PID-based rotation process **/
   private final boolean usePIDRotation;
   /** Flag to keep state on PID-based rotation process **/
   private boolean pidRotationActive;
   /** Telemetry state name for PID-based rotation **/
   private final String autoRotationActiveTelemetryState = "pidRotation";

   /** Current encoder / PID set point for rotation **/
   private FeederPosition positionSetPoint;


   private FeederModule()
   {
      logger.info( "constructing" );

      motor = constructMotor();
      feederRotation = constructEncoder();

      usePIDRotation = PreferencesManager.getInstance().usePIDFeederRotation();

      disablePIDRotation();

      logger.info( "constructed" );
   }


   /**
    * Constructs/initializes the feeder speed controller
    *
    * @return constructed and initialized motor
    */
   private SpeedController constructMotor()
   {
      final int canID = ShooterConfig.getBallFeederChannel();
      final CANTalon sc = new CANTalon( canID );
      final String component = feederMotorName + "[" + canID + "]";
      liveWindow.addActuator( myName, component, sc );

      // Set the brake on the feeder
      sc.enableBrakeMode( true );

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
      sc.setEncPosition( 335062 );
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
      sc.configPeakOutputVoltage( 4, -4 ); // 12 is 100%
      //
      sc.setProfile( 0 );
      sc.setPID( 0.1, 0.0, 0.0 );

      // sc.setAllowableClosedLoopErr( 0 );
      // sc.configEncoderCodesPerRev( 409600 );

      // units are in motor revolutions
      // sc.setForwardSoftLimit( absolutePosition + 0.5 );
      sc.enableForwardSoftLimit( false );
      // sc.setReverseSoftLimit( absolutePosition - 0.5 );
      sc.enableReverseSoftLimit( false );
   }


   /**
    * Constructs/initializes the feeder angle encoder sensor
    *
    * @return constructed and initialized motor
    */
   private FeederRotation constructEncoder()
   {
      final FeederRotation rotation = new FeederRotation( getMotor() );
      return rotation;
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

      positionSetPoint = FeederPosition.UNSET;

      stop();
   }


   /***********************************
    * State Management Section
    ***********************************/

   private void setPIDRotationActive( boolean active )
   {
      smartDashboard.putBoolean( autoRotationActiveTelemetryState, active );
   }


   private void setMotorRunning( boolean motor )
   {
      smartDashboard.putBoolean( feederMotorTelemetryState, motor );
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
   public void enablePIDRotation()
   {
      logger.debug( "enablePIDRotation()" );
      if ( !usePIDRotation )
      {
         logger.warn( "PID-based rotation not enabled in preferences" );
         return;
      }

      setMotorSpeed( 0.0 );

      pidRotationActive = true;
      setPIDRotationActive( pidRotationActive );

      positionSetPoint = FeederPosition.UNSET;

      setMotorRunning( true );

      initializeAutomatic( getMotor() );
   }


   /**
    *
    **/
   public void disablePIDRotation()
   {
      logger.debug( "disablePIDRotation()" );
      if ( !usePIDRotation )
      {
         logger.warn( "PID-based rotation not enabled in preferences" );
         return;
      }

      pidRotationActive = false;
      setPIDRotationActive( pidRotationActive );

      positionSetPoint = FeederPosition.UNSET;

      setMotorRunning( false );

      initializeManual( getMotor() );
   }


   /**
    *
    **/
   public void setPosition( FeederPosition position )
   {
      if ( !pidRotationActive )
      {
         logger.warn( "not active; ignoring setFeederPosition()" );
         return;
      }
      logger.debug( "setting position={}", position );
      System.out.println( "setting to position " + position );

      if ( position == positionSetPoint )
      {
         logger.trace( "already at {} so ignore update", position );
         return;
      }

      // TODO - Implement finding setpoint revolutions from position

      // Get the current rotations (motor)
      final double currentRevs = getMotor().get();
      System.out.print( "########## currentRevs=" + currentRevs );
      // Convert to flap revolutions and then extract full & partial
      final double actualFlapRevs = currentRevs / 163.6;
      final double fullFlapRevs = Math.floor( actualFlapRevs );
      final double partialFlapRevs = actualFlapRevs - fullFlapRevs;
      System.out.println( ", actualFlap=" + actualFlapRevs + ", fullFlap="
         + fullFlapRevs + ", partialFlap=" + partialFlapRevs );

      final double updateRevs =
         correctToShortestPath( partialFlapRevs, position.getPosition() );

      final double finalRevs = fullFlapRevs + updateRevs;
      System.out
         .println( "updateRevs=" + updateRevs + ", finalRevs=" + finalRevs );

      logger.debug( "setting to: {} for position {}",
         StringUtils.format( finalRevs, 3 ), position );
      System.out
         .println( "setting to " + finalRevs + " for position " + position );

      getMotor().set( finalRevs );
   }


   private double correctToShortestPath( double currentRevs, double targetRevs )
   {
      logger.debug( "currentRevs={}, targetRevs={}",
         StringUtils.format( currentRevs, 3 ),
         StringUtils.format( targetRevs, 3 ) );

      double retValue = 0;
      if ( currentRevs < targetRevs )
      {
         final double distance1 = Math.abs( targetRevs - currentRevs );
         final double distance2 = Math.abs( currentRevs + ( 1 - targetRevs ) );

         final double shortest = Math.min( distance1, distance2 );
         logger.debug( "distance1={}, distance2={}, shortest={}",
            StringUtils.format( distance1, 3 ),
            StringUtils.format( distance2, 3 ),
            StringUtils.format( shortest, 3 ) );

         if ( distance1 == shortest )
         {
            retValue = distance1;
         }
         else
         {
            retValue = -distance2;
         }
      }
      else
      {
         final double distance1 = Math.abs( targetRevs - currentRevs );
         final double distance2 = Math.abs( ( 1 - currentRevs ) + targetRevs );

         final double shortest = Math.min( distance1, distance2 );
         logger.debug( "distance1={}, distance2={}, shortest={}",
            StringUtils.format( distance1, 3 ),
            StringUtils.format( distance2, 3 ),
            StringUtils.format( shortest, 3 ) );

         if ( distance1 == shortest )
         {
            retValue = -distance1;
         }
         else
         {
            retValue = distance2;
         }
      }

      return retValue;
   }


   /**
    * + feeds ball to shoot, - unloads ball
    *
    * @param speed
    **/
   public void adjust( double speed )
   {
      if ( pidRotationActive )
      {
         logger.debug( "not active; ignoring adjust()" );
         return;
      }

      speed *= feederScaleFactor;

      setMotorSpeed( fixPolarity( speed ) );
   }


   /**
    * Fixes the polarity of the speeds used on the actual wired motors in order
    * to conform to the input convention that "+" (positive) is "feeding" (to
    * shoot) and "-" (negative) is "unloading" relative to shooter.
    *
    * @param speed
    * @return
    **/
   private double fixPolarity( double speed )
   {
      // This is where you change the sign to fix polarity if necessary
      // Is + out and - in
      return speed;
   }


   /**
    * Sets the speed / inputs to the actual hardware speed controllers for the
    * feeder rotation. Includes updates to the dashboard.
    *
    * @param speed
    **/
   private void setMotorSpeed( double speed )
   {
      smartDashboard.putNumber( feederMotorTelemetrySpeed, speed );
      setMotorRunning( !isZero( speed ) );
      motor.set( speed );
   }

}
