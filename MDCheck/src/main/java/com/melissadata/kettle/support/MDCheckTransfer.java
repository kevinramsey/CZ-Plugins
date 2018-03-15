package com.melissadata.kettle.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class MDCheckTransfer extends ByteArrayTransfer {
	public static class Type {
		private String	id;
		private Object	fields[]	= new Object[0];

		public Type() {
		}

		public void addField(String datum) {
			Object[] newFields = new Object[fields.length + 1];
			for (int i = 0; i < fields.length; i++) {
				newFields[i] = fields[i];
			}
			fields = newFields;
			fields[fields.length - 1] = datum;
		}

		public String getField(int i) {
			return (String) fields[i];
		}

		public String getID() {
			return id;
		}

		public int numFields() {
			return fields.length;
		}

		public void setID(String id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return id + ":" + Arrays.toString(fields);
		}
	}

	public static MDCheckTransfer getInstance() {
		return _instance;
	}
	private static final String		MYTYPENAME	= "mdcheck_type_name";
	private static final int		MYTYPEID	= registerType(MYTYPENAME);
	private static MDCheckTransfer	_instance	= new MDCheckTransfer();

	private MDCheckTransfer() {
	}

	@Override
	public int[] getTypeIds() {
		return new int[] { MYTYPEID };
	}

	@Override
	public String[] getTypeNames() {
		return new String[] { MYTYPENAME };
	}

	@Override
	public void javaToNative(Object object, TransferData transferData) {
// System.out.println("MDCheckTransfer.javaToNative(object=" + object + ", transferData=" + transferData);
		if ((object == null) || !(object instanceof Type)) { return; }
		if (isSupportedType(transferData)) {
			Type datum = (Type) object;
			try {
				// write data to a byte array and then ask super to convert to pMedium
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream writeOut = new DataOutputStream(out);
				byte[] buffer = datum.getID().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);
				writeOut.writeInt(datum.numFields());
				for (int i = 0, length = datum.numFields(); i < length; i++) {
					buffer = datum.getField(i).getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
				}
				buffer = out.toByteArray();
				writeOut.close();
				super.javaToNative(buffer, transferData);
			} catch (IOException ignored) {
			}
		}
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
// System.out.println("MDCheckTransfer.nativeToJava(transferData=" + transferData);
		if (isSupportedType(transferData)) {
// System.out.println("MDCheckTransfer.nativeToJava.1:");
			byte[] buffer = (byte[]) super.nativeToJava(transferData);
// System.out.println("MDCheckTransfer.nativeToJava.2: buffer=" + Arrays.toString(buffer));
			if (buffer == null) { return null; }
			Type datum = new Type();
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				int size = readIn.readInt();
				buffer = new byte[size];
				readIn.read(buffer);
				datum.setID(new String(buffer));
				int count = readIn.readInt();
				for (int i = 0; i < count; i++) {
					size = readIn.readInt();
					buffer = new byte[size];
					readIn.read(buffer);
					datum.addField(new String(buffer));
				}
				readIn.close();
			} catch (IOException ex) {
				return null;
			}
			return datum;
		}
		return null;
	}
}
