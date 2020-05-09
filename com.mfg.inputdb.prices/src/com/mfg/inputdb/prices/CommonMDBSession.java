package com.mfg.inputdb.prices;

import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.Assert;
import org.mfg.mdb.runtime.MDBSession;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.utils.Utils;

public abstract class CommonMDBSession extends MDBSession {

	// private final static SessionMode DEFAULT_MODE = SessionMode.MEMORY;

	private final static SessionMode DEFAULT_MODE = SessionMode.READ_WRITE;

	@SuppressWarnings("unused")
	public CommonMDBSession(String sessionName, File root, SessionMode mode,
			Map<String, String> signatures, String jsonSchema)
			throws IOException {
		super(sessionName, root, DEFAULT_MODE, signatures, jsonSchema);
	}

	public CommonMDBSession(String sessionName, File root,
			Map<String, String> signatures, String jsonSchema)
			throws IOException {
		super(sessionName, root, DEFAULT_MODE, signatures, jsonSchema);
	}

	public CommonMDBSession(String sessionName, File root,
			@SuppressWarnings("unused") SessionMode mode,
			Map<String, String> signatures, String jsonSchema, boolean temporal)
			throws IOException {
		this(sessionName, root, DEFAULT_MODE, signatures, jsonSchema);
		out.println("Creating " + (temporal ? "temporal " : "")
				+ "Chart DB session " + root);
		if (temporal && getMode() != SessionMode.MEMORY) {
			File temp = new File(root, "temporal");
			temp.createNewFile();
			Assert.isTrue(isTemporal(root));
		}
		// setDebug(true);
	}

	@Override
	public int closeAndDelete() throws IOException, TimeoutException {
		Utils.debug_var(381047, "Closing session " + getSessionName() + " ... ");
		int fail = super.closeAndDelete();
		Utils.debug_var(381087, "Closed session " + getSessionName()
				+ ". Number of failures is " + Integer.valueOf(fail));

		if (fail > 0) {
			throw new IOException("Session " + getSessionName()
					+ " was not closed properly");
		}

		return fail;
	}

	public static void log(String msg, Object... args) {
		out.println(String.format(msg, args));
	}

	public static boolean isTemporal(File root) {
		return new File(root, "temporal").exists();
	}

}
