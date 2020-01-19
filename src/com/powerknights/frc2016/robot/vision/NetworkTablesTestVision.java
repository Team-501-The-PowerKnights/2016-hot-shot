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

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;


/**
 * @author first.stu
 **/
public class NetworkTablesTestVision
   implements IVisionSource
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( NetworkTablesTestVision.class.getName() );

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
      ourInstance = new NetworkTablesTestVision();
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

   /** Handle to Network Tables **/
   private final NetworkTable table;
   /** Name of the table published by driver station laptop **/
   private static final String tableName = "/Camera";

   private static final double delta = 0.05;

   private int updateCount;

   /** Network tables key for valid update flag **/
   private final String validUpdateKey = "validUpdate";
   /** **/
   private boolean validUpdate;
   /** Network tables key for number of targets in solution **/
   private final String numTargetsKey = "Num_Targets";
   /** **/
   private long numTargets;
   /** Network tables key for (offset) angle to target **/
   private final String angleKey = "Angle";
   /** **/
   private double angle;
   /** Network tables key for distance to target **/
   private final String distanceKey = "Distance";
   /** **/
   private double distance;

   private final VisionUpdate update = new VisionUpdate();


   private NetworkTablesTestVision()
   {
      logger.info( "constructing" );
      table = NetworkTable.getTable( tableName );
      table.addTableListener( new TestListener() );

      logger.info( "constructed" );
   }

   private class TestListener
      implements ITableListener
   {

      public TestListener()
      {
         numTargets = 0;
         table.putNumber( numTargetsKey, numTargets );
         angle = 0.0;
         table.putNumber( angleKey, angle );
         distance = 0.0;
         table.putNumber( distanceKey, distance );
         // Do the valid update last
         validUpdate = false;
         table.putBoolean( validUpdateKey, validUpdate );
      }


      /*
       * (non-Javadoc)
       *
       * @see
       *
       * edu.wpi.first.wpilibj.tables.ITableListener#valueChanged(edu.wpi.first.
       * wpilibj.tables.ITable, java.lang.String, java.lang.Object, boolean)
       */
      @Override
      public void valueChanged( ITable source, String key, Object value,
         boolean isNew )
      {
         boolean update = false;
         double dValue;
         @SuppressWarnings( "unused" )
         boolean bValue;
         switch ( key )
         {
         case angleKey:
            updateCount++;
            dValue = angle;
            angle = ( (Double) value ).doubleValue();
            if ( Math.abs( dValue - angle ) > delta )
            {
               update = true;
            }
            break;
         case distanceKey:
            updateCount++;
            dValue = distance;
            distance = ( (Double) value ).doubleValue();
            if ( Math.abs( dValue - distance ) > delta )
            {
               update = true;
            }
            break;
         case numTargetsKey:
            updateCount++;
            dValue = numTargets;
            numTargets = (long) ( (Double) value ).doubleValue();
            update = true;
            break;
         case validUpdateKey:
            if ( updateCount != 3 )
            {
               logger.warn(
                  "didn't get req'd updates; updateCount=" + updateCount );
            }
            bValue = validUpdate;
            validUpdate = ( (Boolean) value ).booleanValue();
            if ( !validUpdate )
            {
               update = false;
               updateCount = 0;
            }
            break;
         default:
            break;
         }

         if ( update )
         {
            logger.info(
               "key=" + key + ", value=" + ( (Double) value ).doubleValue() );
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
