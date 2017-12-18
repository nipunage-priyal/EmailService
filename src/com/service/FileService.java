package com.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FileService {
	static Function<String, List<String>> fileReader = (fileName) -> {
		return readFile(fileName);
	};

	static BiFunction<String, String, String> fileWriter = (fileName, content) -> {
		return writeToFile(fileName, content);
	};

	private static String writeToFile(final String fileName, final String content) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(fileName, true));
			bw.write(content + "\n");
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		return "Done Writing";
	}

	private static List<String> readFile(final String fileName) {
		BufferedReader br = null;
		FileReader fr = null;
		final List<String> output = new ArrayList<String>();

		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				output.add(sCurrentLine);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		return output;
	}
}
