/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.subsystems;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.modules.ScissorLiftModule;
import com.powerknights.frc2016.robot.modules.WinchModule;


/**
 * @author first.stu
 **/
public class Lifter
   extends PWMSubsystem
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( Lifter.class.getName() );

   /** Singleton instance of class for all to use **/
   private static Lifter ourInstance;
   /** Name of our subsystem **/
   @SuppressWarnings( "unused" )
   private static final String myName = "Lifter";


   /**
    * Constructs instance of the lifter subsystem. Assumed to be called before
    * any usage of the subsystem; and verifies only called once. Allows
    * controlled startup sequencing of the robot and all it's subsystems.
    **/
   public static synchronized void constructInstance()
   {
      if ( ourInstance != null )
      {
         throw new IllegalStateException( "Lifter Already Constructed" );
      }
      ourInstance = new Lifter();
   }


   /**
    * Returns the singleton instance of the lifter subsystem. If it hasn't been
    * constructed yet, throws an <code>IllegalStateException</code>.
    *
    * @return singleton instance of lifter
    **/
   public static Lifter getInstance()
   {
      if ( ourInstance == null )
      {
         throw new IllegalStateException( "Lifter Not Constructed Yet" );
      }
      return ourInstance;
   }

   /** Module for the winch **/
   private final WinchModule winch;
   /** Module for the scissor lift **/
   private final ScissorLiftModule scissorLift;


   /** Module for the scissor lift **/
   // private final Scissor scissor;

   private Lifter()
   {
      logger.info( "constructing" );

      WinchModule.constructInstance();
      winch = WinchModule.getInstance();

      ScissorLiftModule.constructInstance();
      scissorLift = ScissorLiftModule.getInstance();

      logger.info( "constructed" );
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2016.robot.subsystems.ISubsystem#reset()
    */
   @Override
   public void reset()
   {
      winch.reset();
      scissorLift.reset();
   }

   /***********************************
    * State Management Section
    ***********************************/

   /***********************************
    * Public Control Section
    ***********************************/


   /**
    * Extends the 'hook' on the lifter to the proper height required to hang
    * over the bar on the tower. This method provides a fully automatic
    * implementation of the function.
    **/
   public void hang()
   {
      // TODO - Implement method
   }


   /**
    * Extends the 'hook' on the lifter under 'manual' control, using the speed
    * provided. This method is provided to allow the implementation of the
    * function to be done outside the subsystem.
    * <p>
    * The caller is required to stop the process of the hang function by a
    * specific call to {@link #stopHang()} rather than just setting the speed to
    * '0'.
    *
    * @param speed
    **/
   public void hang( double speed )
   {
      scissorLift.up( ensurePositiveSpeed( speed ) );
   }


   /**
    * Stops
    **/
   public void stopHang()
   {
      scissorLift.stop();
   }


   /**
    * Lifts the robot by winching in the cable, which automatically lowers the
    * scissor lift mechanism as well.
    * <p>
    *
    **/
   public void liftRobot()
   {
      winch.in( 1.0 );
   }


   /**
    *
    **/
   public void stopLift( boolean doScissors )
   {
      winch.stop();
      if ( doScissors )
      {
         scissorLift.stop();
      }
   }


   /***********************************
    * Public Test Section
    ***********************************/

   public void liftScissorLift( double speed )
   {
      scissorLift.up( speed );
   }


   public void lowerScissorLift( double speed )
   {
      scissorLift.down( speed );
   }


   public void winchUp( double speed )
   {
      winch.in( speed );
   }


   public void winchDown( double speed )
   {
      winch.out( speed );
   }

}
