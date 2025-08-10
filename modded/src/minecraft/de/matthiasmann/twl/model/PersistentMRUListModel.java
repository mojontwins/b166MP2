package de.matthiasmann.twl.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class PersistentMRUListModel extends SimpleMRUListModel {
	private final Class clazz;
	private final Preferences prefs;
	private final String prefKey;

	public PersistentMRUListModel(int maxEntries, Class clazz, Preferences prefs, String prefKey) {
		super(maxEntries);
		if(clazz == null) {
			throw new NullPointerException("clazz");
		} else if(prefs == null) {
			throw new NullPointerException("prefs");
		} else if(prefKey == null) {
			throw new NullPointerException("prefKey");
		} else {
			this.clazz = clazz;
			this.prefs = prefs;
			this.prefKey = prefKey;
			int numEntries = Math.min(prefs.getInt(this.keyForNumEntries(), 0), maxEntries);

			for(int i = 0; i < numEntries; ++i) {
				Serializable entry = null;
				if(clazz == String.class) {
					entry = (Serializable)clazz.cast(prefs.get(this.keyForIndex(i), (String)null));
				} else {
					byte[] data = prefs.getByteArray(this.keyForIndex(i), (byte[])null);
					if(data != null && data.length > 0) {
						entry = this.deserialize(data);
					}
				}

				if(entry != null) {
					this.entries.add(entry);
				}
			}

		}
	}

	public void addEntry(Serializable entry) {
		if(!this.clazz.isInstance(entry)) {
			throw new ClassCastException();
		} else {
			super.addEntry(entry);
		}
	}

	protected void saveEntries() {
		for(int i = 0; i < this.entries.size(); ++i) {
			Serializable obj = (Serializable)this.entries.get(i);
			if(this.clazz == String.class) {
				this.prefs.put(this.keyForIndex(i), (String)obj);
			} else {
				byte[] data = this.serialize(obj);

				assert data != null;

				this.prefs.putByteArray(this.keyForIndex(i), data);
			}
		}

		this.prefs.putInt(this.keyForNumEntries(), this.entries.size());
	}

	protected byte[] serialize(Serializable obj) {
		try {
			ByteArrayOutputStream ex = new ByteArrayOutputStream();
			DeflaterOutputStream dos = new DeflaterOutputStream(ex, new Deflater(9));

			try {
				ObjectOutputStream oos = new ObjectOutputStream(dos);
				oos.writeObject(obj);
				oos.close();
			} finally {
				this.close(dos);
			}

			return ex.toByteArray();
		} catch (IOException iOException9) {
			this.getLogger().log(Level.SEVERE, "Unable to serialize MRU entry", iOException9);
			return new byte[0];
		}
	}

	protected Serializable deserialize(byte[] data) {
		try {
			ByteArrayInputStream ex = new ByteArrayInputStream(data);
			InflaterInputStream iis = new InflaterInputStream(ex);

			Serializable serializable7;
			try {
				ObjectInputStream ois = new ObjectInputStream(iis);
				Object obj = ois.readObject();
				if(!this.clazz.isInstance(obj)) {
					this.getLogger().log(Level.WARNING, "Deserialized object of type " + obj.getClass() + " expected " + this.clazz);
					return null;
				}

				serializable7 = (Serializable)this.clazz.cast(obj);
			} finally {
				this.close(iis);
			}

			return serializable7;
		} catch (Exception exception11) {
			this.getLogger().log(Level.SEVERE, "Unable to deserialize MRU entry", exception11);
			return null;
		}
	}

	protected String keyForIndex(int idx) {
		return this.prefKey + "_" + idx;
	}

	protected String keyForNumEntries() {
		return this.prefKey + "_entries";
	}

	private void close(Closeable c) {
		try {
			c.close();
		} catch (IOException iOException3) {
			this.getLogger().log(Level.WARNING, "exception while closing stream", iOException3);
		}

	}

	Logger getLogger() {
		return Logger.getLogger(PersistentMRUListModel.class.getName());
	}

	public void addEntry(Object object1) {
		this.addEntry((Serializable)object1);
	}
}
