/**
 * Copyright (c) Team 501 Power Knights 2015. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
 **/

package com.powerknights.frc2016.robot.controllers;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;
import com.powerknights.frc2016.robot.config.DelayTimeConfig;
import com.powerknights.frc2016.robot.config.hw.GamepadModels;
import com.powerknights.frc2016.robot.managers.PreferencesManager;
import com.powerknights.frc2016.robot.ophmi.OperatorGamepad;
import com.powerknights.frc2016.robot.subsystems.HMIControllers;
import com.powerknights.frc2016.utils.Controller;

import edu.wpi.first.wpilibj.Joystick;


/**
 * @author first.stu
 **/
public class OperatorController
   extends Controller
{

   /** Our classes' logger **/
   private static final Logger logger =
      RioLogger.getLogger( OperatorController.class.getName() );

   /** Handle to operator gamepad **/
   private final IHmiController pad;


   public OperatorController()
   {
      super( "Operator Controller",
         DelayTimeConfig.getOperatorControllerDelay() );

      final PreferencesManager prefs = PreferencesManager.getInstance();
      final String controller = prefs.getOperatorController();

      final HMIControllers controls = HMIControllers.getInstance();
      final Joystick joystick = controls.getOperatorController();
      // Two person control of lift function required
      final Joystick driverJoystick = controls.getDriverController();

      if ( controller.equals( "LogitechDualActionGamepad" ) )
      {
         pad = new OperatorGamepad( joystick, GamepadModels.LogitechDualAction,
            driverJoystick );
         setConfiguration( "operatorPad", "Logitech Dual Action" );
      }
      else if ( controller.equals( "Xbox360Gamepad" ) )
      {
         pad = new OperatorGamepad( joystick, GamepadModels.Xbox360,
            driverJoystick );
         setConfiguration( "operatorPad", "Xbox 360" );
      }
      else if ( controller.equals( "LogitechF310Gamepad" ) )
      {
         pad = new OperatorGamepad( joystick, GamepadModels.LogitechF310,
            driverJoystick );
         setConfiguration( "operatorPad", "Logitech F310" );
      }
      else
      {
         logger.error( "Unknown config for OperatorController: " + controller );
         pad = new OperatorGamepad( joystick, GamepadModels.Default,
            driverJoystick );
         setConfiguration( "operatorPad",
            "Default[" + GamepadModels.Default + "]" );
      }
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2014.utils.Controller#setUp()
    */
   @Override
   public void setUp()
   {
      pad.initForModeStart();
   }


   /*
    * (non-Javadoc)
    *
    * @see com.powerknights.frc2012.utils.Controller#doIt()
    */
   @Override
   public boolean doIt()
   {
      // Look at current operator pad values and process them
      pad.performUpdate();

      // Keep on going
      return false;
   }

}
