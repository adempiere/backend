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
public class Server {
	/**	Host Name	*/
	private String host;
	/**	Port	*/
	private int port;
	/**	Certificate Chain	*/
	private String certificate_chain_file;
	/**	Private Key	*/
	private String private_key_file;
	/**	Trust Certificate	*/
	private String trust_certificate_collection_file;
	/**
	 * Default constructor
	 * @param host
	 * @param port
	 * @param certificate_chain_file
	 * @param private_key_file
	 * @param trust_certificate_collection_file
	 */
	public Server(String host, int port, String certificate_chain_file, String private_key_file, String trust_certificate_collection_file) {
		this.host = host;
		this.port = port;
		this.certificate_chain_file = certificate_chain_file;
		this.private_key_file = private_key_file;
		this.trust_certificate_collection_file = trust_certificate_collection_file;
	}
	
	/**
	 * Default constructor without parameters
	 */
	public Server() {
		
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
	 * @return the certificate_chain_file
	 */
	public final String getCertificate_chain_file() {
		return certificate_chain_file;
	}

	/**
	 * @return the private_key_file
	 */
	public final String getPrivate_key_file() {
		return private_key_file;
	}

	/**
	 * @return the trust_certificate_collection_file
	 */
	public final String getTrust_certificate_collection_file() {
		return trust_certificate_collection_file;
	}

	/**
	 * @return the isTlsEnabled
	 */
	public final boolean isTlsEnabled() {
		return getCertificate_chain_file() != null 
				&& getPrivate_key_file() != null;
	}

	@Override
	public String toString() {
		return "Server [host=" + host + ", port=" + port + ", certificate_chain_file=" + certificate_chain_file
				+ ", private_key_file=" + private_key_file + ", trust_certificate_collection_file="
				+ trust_certificate_collection_file + "]";
	}
}
