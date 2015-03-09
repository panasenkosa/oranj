
/*
 * Copyright (C) 2010 Sergey Panasenko
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */

package org.oranj.convert.standards;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.oranj.exceptions.DBObjectConversionException;

public class LobConversions {

	public static Object clobToString(Object value) throws DBObjectConversionException {
		oracle.sql.CLOB clob = (oracle.sql.CLOB) value;
		BufferedReader reader = null;
		CharArrayWriter writer = null;
		String result = null;
		try {
			reader = new BufferedReader(clob.getCharacterStream());		
			writer = new CharArrayWriter();
			String line = null;
			while ((line = reader.readLine()) != null) {
				writer.write(line);			
			}
			result = new String(writer.toCharArray());			
		} catch (IOException e) {
			throw new DBObjectConversionException("I/O error casting oracle CLOB to string", e);
		} catch (SQLException e) {
			throw new DBObjectConversionException("Sql exception casting oracle CLOB to string", e);
		}
		finally {
			if (reader!=null)
				try {
					reader.close();
				} catch (IOException e) {}
			if (writer!=null)
				writer.close();			
		}


		return result;
	}

	public static Object stringToClob(Object value, Connection connection) throws DBObjectConversionException {
		
		oracle.sql.CLOB clob = null;
		try {
			clob = oracle.sql.CLOB.createTemporary(connection, true,
					oracle.sql.CLOB.DURATION_SESSION);
			clob.setString(1, (String) value);
		} catch (SQLException e) {
			throw new DBObjectConversionException("Sql exception casting string to oracle CLOB", e);
		}
		return clob;
	}
	
	public static Object byteArrayToBlob(Object value, Connection connection) throws DBObjectConversionException {

		OutputStream out = null;
		oracle.sql.BLOB blob = null;
		try {
			blob = oracle.sql.BLOB.createTemporary(connection,
					true, oracle.sql.BLOB.DURATION_SESSION);
			out = blob.getBinaryOutputStream();
			out.write((byte[])value);			
		} catch (SQLException e) {
			throw new DBObjectConversionException("Sql exception casting byte[] to oracle BLOB", e);
		} catch (IOException e) {
			throw new DBObjectConversionException("I/O exception casting byte[] to oracle BLOB", e);
		}
		finally {
			if (out!=null)
				try {
					out.close();
				} catch (IOException e) {}
		}
		return blob;
		
	}	
	
	public static Object blobToByteArray(Object value) throws DBObjectConversionException {

		oracle.sql.BLOB blob = (oracle.sql.BLOB) value;
		byte[] result = null;
		InputStream in = null;
		ByteArrayOutputStream baos = null;
		try {
			in = blob.getBinaryStream();
			baos = new ByteArrayOutputStream() ;
			int iRead=0 ;
			byte[] baChunk = new byte[4096] ;
			while ((iRead=in.read(baChunk))>0)
				baos.write(baChunk,0,iRead) ;
			result = baos.toByteArray() ;
		} catch (SQLException e) {
			throw new DBObjectConversionException("Sql exception casting oracle BLOB to byte[]", e);
		} catch (IOException e) {
			throw new DBObjectConversionException("I/O exception casting oracle BLOB to byte[]", e);
		}
		 finally {
			if (in!=null)
				try {
					in.close();
				} catch (IOException e) {}
			if (baos!=null)
				try {
					baos.close();
				} catch (IOException e) {}
		}
		return result;
	}		
}
