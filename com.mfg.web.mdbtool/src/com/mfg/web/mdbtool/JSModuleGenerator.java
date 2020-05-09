package com.mfg.web.mdbtool;

import static java.lang.System.out;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.mfg.inputdb.indicator.mdb.BandsMDB;
import com.mfg.inputdb.indicator.mdb.PivotMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB;

public class JSModuleGenerator {

	private LinkedList<Class<?>> _mdbClasses;

	public JSModuleGenerator() {
		_mdbClasses = new LinkedList<>();
	}

	public void addMdbClass(Class<?> mdbClass) {
		_mdbClasses.add(mdbClass);
	}

	public void generate(OutputStream output) throws Exception {
		String tab = "\t";
		String tabtab = tab + tab;
		String tabtabtab = tab + tab + tab;

		Map<Class<?>, String> methodNameMap = new HashMap<>();
		methodNameMap.put(byte.class, "getInt" + Byte.SIZE);
		methodNameMap.put(boolean.class, "getInt" + Byte.SIZE);
		methodNameMap.put(int.class, "getInt" + Integer.SIZE);

		// javascript does not support Int64, so we simulate it with Int32
		methodNameMap.put(long.class, "getInt" + Integer.SIZE);

		methodNameMap.put(float.class, "getFloat" + Float.SIZE);
		methodNameMap.put(double.class, "getFloat" + Double.SIZE);

		List<String> lines = new ArrayList<>();

		lines.add("/*jslint bitwise:true*/");
		lines.add("var MFGDataView = {");

		for (Class<?> cls : _mdbClasses) {

			String[] split = cls.getName().split(Pattern.quote("."));
			String clsName = split[split.length - 1];
			clsName = clsName.substring(0, clsName.length() - 3);

			String[] COLUMNS_NAME = getFieldValue(cls, "COLUMNS_NAME");
			boolean[] COLUMNS_IS_VIRTUAL = getFieldValue(cls,
					"COLUMNS_IS_VIRTUAL");
			Class<?>[] COLUMNS_TYPE = getFieldValue(cls, "COLUMNS_TYPE");
			int[] COLUMNS_OFFSET = getFieldValue(cls, "COLUMN_OFFSET");
			@SuppressWarnings("boxing")
			int RECORD_SIZE = getFieldValue(cls, "RECORD_SIZE");

			// getXXXRecord

			lines.add("");
			lines.add(tab + "get" + clsName
					+ "Record: function (dataview, index) {");
			lines.add(tabtab + "'use strict';");
			lines.add(tabtab + "var startByte = index * " + RECORD_SIZE + ";");
			lines.add(tabtab + "return {");

			for (int i = 0; i < COLUMNS_NAME.length; i++) {
				boolean lastCol = i == COLUMNS_NAME.length - 1;
				String colname = COLUMNS_NAME[i];
				if (!COLUMNS_IS_VIRTUAL[i]) {
					Class<?> coltype = COLUMNS_TYPE[i];
					int offset = COLUMNS_OFFSET[i];

					// since javascript does not support Int64, we simulate it
					// with Int32, so we add an extra 4 bytes to the offset
					if (coltype == long.class) {
						offset += 4;
					}

					String expr = "dataview." + methodNameMap.get(coltype)
							+ "(startByte"
							+ (offset == 0 ? "" : " + " + offset) + ")";

					if (coltype == boolean.class) {
						expr += " === 1";
					}

					String comment = coltype == long.class ? " // simulate Int64 with Int32"
							: "";

					lines.add(tabtabtab + colname + ": " + expr
							+ (lastCol ? "" : ",") + comment);
				}
			}

			lines.add(tabtab + "};");
			boolean isLastMDBClass = cls == _mdbClasses.getLast();
			lines.add(tab + "},");

			// getXXXRecordCount
			lines.add("");
			lines.add(tab + "get" + clsName
					+ "RecordCount: function (bytesCount) {");
			lines.add(tabtab + "'use strict';");
			lines.add(tabtab + "return (bytesCount / " + RECORD_SIZE
					+ ")  | 0;");
			lines.add(tab + "}" + (isLastMDBClass ? "" : ","));
		}

		lines.add("};");

		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line + "\n");
		}

		output.write(sb.toString().getBytes());
		output.flush();
	}

	@SuppressWarnings("unchecked")
	private static <T> T getFieldValue(Class<?> cls, String name)
			throws Exception {
		return (T) cls.getField(name).get(cls);
	}

	public static void main(String[] args) throws Exception {
		JSModuleGenerator generator = new JSModuleGenerator();

		generator.addMdbClass(PriceMDB.class);
		generator.addMdbClass(PivotMDB.class);
		generator.addMdbClass(BandsMDB.class);

		Path p = Paths.get(".").toAbsolutePath().getParent().getParent()
				.resolve("com.mfg.web/www/app/services/data-view.js");

		out.println("Write to " + p);

		try (OutputStream output = Files.newOutputStream(p)) {
			generator.generate(output);
		}

		out.println(new String(Files.readAllBytes(p)));
	}
}
