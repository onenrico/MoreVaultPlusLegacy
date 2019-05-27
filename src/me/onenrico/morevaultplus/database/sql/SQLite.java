//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.database.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.onenrico.morevaultplus.main.Core;
import me.onenrico.morevaultplus.utils.MessageUT;
import me.onenrico.morevaultplus.utils.SqlUT;

public class SQLite extends Database {
	public static String dbname;

	static {
		SQLite.dbname = "database";
	}

	@Override
	public void load() {
		connection = getSQLConnection();
		SqlUT.con = connection;
	}

	@Override
	public Connection getSQLConnection() {
		final File dataFolder = new File(Core.getThis().getDataFolder() + "/data/");
		final File dataFile = new File(Core.getThis().getDataFolder() + "/data/",
				String.valueOf(SQLite.dbname) + ".db");
		if (!dataFolder.exists()) {
			try {
				dataFolder.mkdir();
				dataFile.createNewFile();
			} catch (IOException e) {
				MessageUT.cmsg("File write error: " + SQLite.dbname + ".db");
			}
		}
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			return connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
		} catch (SQLException ex) {
			MessageUT.cmsg("G: SQLite exception on initialize");
		} catch (ClassNotFoundException ex2) {
			MessageUT.cmsg("H: SQLite exception on initialize");
		}
		return null;
	}
}
