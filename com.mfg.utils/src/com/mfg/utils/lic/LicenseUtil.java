package com.mfg.utils.lic;

import static java.lang.System.out;

import java.time.LocalDate;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class LicenseUtil {

	private static final String LIC_KEY = "com.mfg.lic";
	private static Preferences _prefNode;

	static {
		_prefNode = Preferences.userRoot();
	}

	/**
	 * Register a new product key.
	 * 
	 * @param licKey
	 *            The new key.
	 * @return If the key is valid return null, else return the error message
	 *         that should be shown to the user.
	 * @throws BackingStoreException
	 */
	public static String registerLicKey(String licKey) {
		String result = validateLicKey(licKey);
		if (result == null) {
			_prefNode.put(LIC_KEY, licKey);
			try {
				_prefNode.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
				return e.getMessage();
			}
		}
		return result;
	}

	/**
	 * Check if the current product instance is valid.
	 * 
	 * @return If the product is valid return null, else return an error message
	 *         that should be shown to the user.
	 */
	public static String isValidLicense() {
		String k = _prefNode.get(LIC_KEY, null);
		return validateLicKey(k);
	}

	/**
	 * Validate the key.
	 * 
	 * @param licKey
	 *            The key.
	 * @return Return null if the key is valid, else return an error message.
	 */
	private static String validateLicKey(String licKey) {
		String result;
		if (licKey == null) {
			result = "This is a demo application. Please contact with MFG team to get a lisence key.";
		} else {
			try {
				result = null;
				String msg = Encrypter.decrypt(licKey);
				if (!msg.equals("DoNotExpire")) {
					// day-month-year
					String[] split = msg.split("-");
					LocalDate date = LocalDate.now();

					int dd = date.getDayOfMonth();
					int mm = date.getMonthValue();
					int yy = date.getYear();

					if (dd > Integer.parseInt(split[0])
							|| mm > Integer.parseInt(split[1])
							|| yy > Integer.parseInt(split[2])) {
						result = "Your product key expired. Please contact with MFG team to get a lisence key.";
					}
				}
			} catch (Exception e) {
				result = "Your product key is not valid. Please contact with MFG team to get a lisence key.";
			}
		}

		if (result != null) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"License Key", result);
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		LocalDate date = LocalDate.now();

		String rawMessage = date.getDayOfMonth() + "-" + date.getMonthValue()
				+ "-" + date.getYear();

		rawMessage = "21-12-2014";
		rawMessage = "DoNotExpire";

		String key = Encrypter.encrypt(rawMessage);
		out.println(key);

		String rawMessage2 = Encrypter.decrypt(key);
		out.println(rawMessage2);

		validateLicKey(key);
	}

}
