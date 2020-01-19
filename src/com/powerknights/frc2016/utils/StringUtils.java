/**
 * Copyright (c) Team 501 Power Knights 2015, 2016. All Rights Reserved. Open
 * Source Software - May be modified and shared by FRC teams. The code must be
 * accompanied by the FIRST BSD license file in the root directory of the
 * project. You may also obtain a copy of it from the following:
 * http://www.opensource.org/licenses/bsd-license.php.
 *
 * See (Git) repository metadata for author and revision history for this file.
 **/

package com.powerknights.frc2016.utils;


import org.slf4j.Logger;

import com.powerknights.frc2016.riolog.RioLogger;


/**
 * @author first.stu
 **/
public class StringUtils
{

   /** Our classes' logger **/
   @SuppressWarnings( "unused" )
   private static final Logger logger =
      RioLogger.getLogger( StringUtils.class.getName() );


   /**
    * Formats the provided value to have the specified number of digits of
    * precision to the right of the decimal point.
    * <p>
    * Includes a "+" sign if the value is positive, and a " " if the value is
    * 'exactly' zero.
    *
    * @param value - value to format
    * @param digits - number of digits of precision
    * @return string with value formatted as specified
    **/
   static public String format( double value, int digits )
   {
      final String valueString = Double.toString( value );
      final int dotLoc = valueString.indexOf( '.' );
      final int numDigits = valueString.length() - dotLoc - 1;

      final StringBuffer buf = new StringBuffer( valueString );
      if ( numDigits < digits )
      {
         for ( int i = 0; i < ( digits - numDigits ); i++ )
         {
            buf.append( "0" );
         }
      }
      else if ( numDigits == digits )
      {
         // lucky conversion
      }
      else
      {
         buf.setLength( ( dotLoc + digits + 1 ) );
      }

      if ( value > 0 )
      {
         buf.insert( 0, '+' );
      }
      if ( ( buf.charAt( 0 ) != '+' ) && ( buf.charAt( 0 ) != '-' ) )
      {
         buf.insert( 0, ' ' );
      }

      return buf.toString();
   }

}
