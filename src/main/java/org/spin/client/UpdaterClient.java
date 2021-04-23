/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program.	If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.client;

import java.util.logging.Logger;

import org.spin.base.util.UpdateManager;

public class UpdaterClient {
	  private static final Logger logger = Logger.getLogger(UpdaterClient.class.getName());
	  
	  /**
	   * Greet server. If provided, the first element of {@code args} is the name to use in the
	   * greeting.
	   */
	  public static void main(String[] args) throws Exception {
		  logger.info("####################### Packages #####################");
		  UpdateManager.newInstance()
		  	.withHost("localhost")
	    	.withPort(50059)
	    	.withToken(args[0])
	    	.listAvailablePackages("", "2020-12-01", null)
	    	.getPackagesList().forEach(packageValue -> logger.info("Package: " + packageValue));
		  logger.info("####################### Updates #####################");
		  UpdateManager.newInstance()
		  	.withHost("localhost")
	    	.withPort(50059)
	    	.withToken(args[0])
	    	.listUpdates("D", null)
	    	.getUpdatesList().forEach(packageValue -> logger.info("Updates: " + packageValue));
		  logger.info("####################### Steps #####################");
		  UpdateManager.newInstance()
		  	.withHost("localhost")
	    	.withPort(50059)
	    	.withToken(args[0])
	    	.listSteps("0a229f92-8933-499b-9ad0-56489dd9fbf9", null)
	    	.getStepsList().forEach(packageValue -> logger.info("Steps: " + packageValue));
		  
	  }
}
