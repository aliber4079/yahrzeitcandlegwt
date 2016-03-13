/*
   $Header: /cvsroot/hebcal/java/hebcalJ/src/main/java/net/sf/hebcal/HebrewDateException.java,v 1.1 2007/01/07 13:13:13 sadinoff Exp $
   Hebcal - A Jewish Calendar Generator
   Copyright (C) 1994-2006  Danny Sadinoff

   http://sourceforge.net/projects/hebcal

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License
   as published by the Free Software Foundation; either version 2
   of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   Danny Sadinoff can be reached at 

   danny@sadinoff.com
 */

package net.sf.hebcal;

/** 
* Exception for the HebrewDate class.
*/
public class HebrewDateException extends Exception
{
	public HebrewDateException(String msg)
	{
		super(msg);
	}
}
