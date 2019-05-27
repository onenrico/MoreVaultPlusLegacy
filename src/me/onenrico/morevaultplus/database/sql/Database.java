//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.onenrico.morevaultplus.utils.MessageUT;

public abstract class Database {
	public Connection connection;

	public abstract Connection getSQLConnection();

	public abstract void load();

	public void close(final PreparedStatement ps, final ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException ex) {
			MessageUT.cmsg("F: " + ex);
		}
	}
}
