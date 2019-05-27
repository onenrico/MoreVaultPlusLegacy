//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class NBTFile extends NBTCompound {
	private final File file;
	private Object nbt;

	public NBTFile(final File file) throws IOException {
		super(null, null);
		this.file = file;
		if (file.exists()) {
			final FileInputStream inputsteam = new FileInputStream(file);
			nbt = NBTReflectionUtil.readNBTFile(inputsteam);
		} else {
			nbt = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
			save();
		}
	}

	public void save() throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		final FileOutputStream outStream = new FileOutputStream(file);
		NBTReflectionUtil.saveNBTFile(nbt, outStream);
	}

	public File getFile() {
		return file;
	}

	@Override
	protected Object getCompound() {
		return nbt;
	}

	@Override
	protected void setCompound(final Object compound) {
		nbt = compound;
	}
}
