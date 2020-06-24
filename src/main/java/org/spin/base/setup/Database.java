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
public class Database {
	/**	Host Name	*/
	private String host;
	/**	Port	*/
	private int port;
	/**	User Name	*/
	private String user;
	/**	Password	*/
	private String password;
	/**	Database	*/
	private String name;
	
	/**
	 * Default constructor
	 * @param host
	 * @param port
	 */
	public Database(String host, int port, String user, String password, String name) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.name = name;
	}
	
	/**
	 * Default constructor without parameters
	 */
	public Database() {
		
	}

	/**
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @return the user
	 */
	public final String getUser() {
		return user;
	}

	/**
	 * @return the password
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Server [host=" + host + ", port=" + port + ", user=" + user + ", password=******, database="
				+ name + "]";
	}
}
