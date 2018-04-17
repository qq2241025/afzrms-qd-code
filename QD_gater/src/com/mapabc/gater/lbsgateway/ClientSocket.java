/**
 * 
 */
package com.mapabc.gater.lbsgateway;

import java.net.Socket;
import java.util.Date;

/**
 * @author 
 * 
 */
public class ClientSocket {
	private Date date;
	private Socket socket;

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * @param socket
	 *            the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.socket == null) ? 0 : this.socket.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ClientSocket other = (ClientSocket) obj;
		if (this.socket == null) {
			if (other.socket != null)
				return false;
		} else if (!this.socket.equals(other.socket))
			return false;
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String ret =  "socket tcplink:" + this.getSocket().isClosed() + ",connected:"
				+ this.getSocket().isConnected() + ",isInputShutdown:"
				+ this.getSocket().isInputShutdown() + ",isOutputShutdown:"
				+ this.getSocket().isOutputShutdown()+",remote addr:";
		if (null  != this.getSocket().getRemoteSocketAddress()){
			ret += this.getSocket().getRemoteSocketAddress().toString();
		}
		
		return ret;
	}

}
