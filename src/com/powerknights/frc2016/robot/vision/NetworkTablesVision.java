/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.vision;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.Level;
import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.managers.LCDManager;
import com.powerknights.frc2016.robot.managers.SmartDashboardManager;
import com.powerknights.frc2016.utils.StringUtils;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;


/**
 * @author first.stu
 **/
public class NetworkTablesVision
   implements IVisionSource
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( NetworkTablesVision.class.getName() );


   static
   {
      // LOGGER - Override of default level
      RioLogger.setLevel( logger, Level.TRACE );
   }

   /** Singleton instance of class for all to use **/
   private static IVisionSource ourInstance;


   /**
    * Constructs instance of the NetworkTables-based <code>IVisionSource</code>.
    * Assumed to be called before any usage of the subsystem; and verifies only
    * called once. Allows controlled startup sequencing of the robot and all
    * it's subsystems.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException(
            "NetworkTables Server Already Constructed" );
      }
      ourInstance = new NetworkTablesVision();
   }


   /**
    * Returns the singleton instance of the <code>IVisionSource</code>. If it
    * hasn't been constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of vision source
    **/
   public static IVisionSource getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException(
            "NetworkTables Server Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Handle to smart dashboard **/
   private final SmartDashboardManager smartDashboard;
   /** Handle to LCD on dashboard **/
   private final LCDManager lcdMgr;

   /** Handle to Network Tables **/
   private final NetworkTable table;
   /** Name of the table published by driver station laptop **/
   private static final String tableName = "/Camera";

   /** **/
   @SuppressWarnings( "unused" )
   private long updateCount;
   /** **/
   private long validCount;
   /** **/
   private long invalidCount;

   /** Network tables key for valid update flag **/
   private final String validUpdateKey = "validUpdate";
   /** Telemetry state name for update **/
   private final String validUpdateTelemetryState = "visionValidUpdate";
   /** Network tables key for (offset) angle to target **/
   private final String angleKey = "Angle";
   /** Telemetry angle name for update **/
   private final String visionTelemetryAngle = "visionAngle";
   /** Network tables key for distance to target **/
   private final String distanceKey = "Distance";
   /** Telemetry distance name for update **/
   private final String visionTelemetryDistance = "visionDistance";

   /** Telemetry distance name for update **/
   private final String visionTelemetryLock = "targetLock";

   private final VisionUpdate update = new VisionUpdate();


   private NetworkTablesVision()
   {
      logger.info( "constructing" );

      smartDashboard = SmartDashboardManager.getInstance();
      lcdMgr = LCDManager.getInstance();

      lcdMgr.setVisionStarted();

      table = NetworkTable.getTable( tableName );
      if ( table.isConnected() )
      {
         lcdMgr.setVisionConnected();
      }

      table.putNumber( angleKey, 0.0 );
      table.putNumber( distanceKey, 0.0 );
      // Do the valid update last
      table.putBoolean( validUpdateKey, false );

      table.addTableListener( new UpdateListener() );

      updateCount = 0;
      validCount = 0;
      invalidCount = 0;

      logger.info( "constructed" );
   }

   private class UpdateListener
      implements ITableListener
   {

      private boolean hasUpdated;


      public UpdateListener()
      {
         hasUpdated = false;

         // Update the dashboard to initialize it
         final VisionUpdate update = new VisionUpdate();
         update.setValid( false );
         updateDashboard( update );
      }

      //
      private long lcdCount = 0;


      private void updateDashboard( VisionUpdate update )
      {
         // System.out.println( "updateDashboard() in RoboRealmVision" );
         smartDashboard.putBoolean( validUpdateTelemetryState,
            update.isValid() );

         if ( update.isValid() )
         {
            smartDashboard.putNumber( visionTelemetryAngle, update.angle );
            smartDashboard.putNumber( visionTelemetryDistance,
               update.distance );
            // Calculated state
            smartDashboard.putBoolean( visionTelemetryLock,
               ( update.angle < 5 ) );
         }
         else
         {
            smartDashboard.putNumber( visionTelemetryAngle, 0 );
            smartDashboard.putNumber( visionTelemetryDistance, 0 );
            // Calculated state
            smartDashboard.putBoolean( visionTelemetryLock, false );
         }

         logger.trace( "distance={}, angle={}, valid={}",
            StringUtils.format( update.distance, 2 ),
            StringUtils.format( update.angle, 2 ), update.isValid() );
      }


      /*
       * (non-Javadoc)
       *
       * @see
       * edu.wpi.first.wpilibj.tables.ITableListener#valueChanged(edu.wpi.first.
       * wpilibj.tables.ITable, java.lang.String, java.lang.Object, boolean)
       */
      @Override
      public void valueChanged( ITable source, String key, Object value,
         boolean isNew )
      {
         if ( !hasUpdated )
         {
            lcdMgr.setVisionUpdating();
            hasUpdated = true;
         }

         try
         {
            switch ( key )
            {
            case angleKey:
               update.angle = ( (Double) value ).doubleValue();
               logger.trace( "update.angle = {}", update.angle );
               break;
            case distanceKey:
               update.distance = ( (Double) value ).doubleValue();
               logger.trace( "update.distance = {}", update.distance );
               break;
            case validUpdateKey:
               update.setValid( ( (Boolean) value ).booleanValue() );
               logger.trace( "update.valid = {}", update.isValid() );
               updateCount++;

               updateDashboard( update );

               if ( update.isValid() )
               {
                  validCount++;
               }
               else
               {
                  invalidCount++;
               }

               // Arrive every 40 msec; so update 1 per second
               lcdCount++;
               if ( lcdCount > 5 )
               {
                  lcdMgr.updateVision( validCount, invalidCount );
                  lcdCount = 0;
               }
               break;
            default:
               logger.error( "unknown key: {}", key );
               break;
            }
         }
         catch (

         final Exception ex )

         {
            // TODO - Do we handle exception? Probably not ...
         }
      }

   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.vision.IVisionSource#getUpdate()
    */
   @Override
   public VisionUpdate getUpdate()
   {
      return update;
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.vision.IVisionSource#haveUpdate()
    */
   @Override
   public boolean haveUpdate()
   {
      return false;
   }

}
