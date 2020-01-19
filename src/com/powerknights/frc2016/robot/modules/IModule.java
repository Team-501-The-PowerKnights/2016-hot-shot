/**
 * Copyright (c) Team 501 Power Knights 2016. All Rights Reserved. Open Source
 * Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 **/

package com.powerknights.frc2016.robot.modules;


/**
 * @author first.stu
 **/
public interface IModule
{

   /**
    * Resets all state and ensures that any active components (e..g, PWMs) are
    * set to their 'off' state.
    **/
   public void reset();

}