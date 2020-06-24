/*************************************************************************************
 * Product: ADempiere Bot                                                            *
 * Copyright (C) 2012-2019 E.R.P. Consultores y Asociados, C.A.                      *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                      *
 * This program is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU General Public License as published by              *
 * the Free Software Foundation, either version 3 of the License, or                 *
 * (at your option) any later version.                                               *
 * This program is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                     *
 * GNU General Public License for more details.                                      *
 * You should have received a copy of the GNU General Public License                 *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.base.setup;

/**
 * Determinate all ADempiere client setup values for Human Resource
 * @author Yamel Senih
 */
public class SetupWrapper {
	/**	Server	*/
	private Server server;
	/**	Database	*/
	private Database database;
	
	/**
	 * Default constructor
	 * @param server
	 * @param database
	 */
	public SetupWrapper(Server server, Database database) {
		this.server = server;
		this.database = database;
	}
	
	/**
	 * Default without parameters
	 */
	public SetupWrapper() {
		
	}
	
	/**
	 * @return the database
	 */
	public final Database getDatabase() {
		return database;
	}

	/**
	 * @return the server
	 */
	public final Server getServer() {
		return server;
	}

	@Override
	public String toString() {
		return "SetupWrapper [server=" + server + ", database=" + database + "]";
	}
}
