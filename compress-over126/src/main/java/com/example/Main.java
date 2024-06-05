package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class Main {
	public static void main(String[] args) {
		// 読み取り対象のzipファイルパス
		String targetPath = getTargetPath();

		// ZIPファイルを読み込む（< 1.26）
		System.out.print("Deprecated: ");
		readTxtInZip_deprecated(targetPath);

		// ZIPファイルを読み込む（1.26 =<）
		System.out.print("New       : ");
		readTxtInZip_new(targetPath);
	}

	/**
	 * zipファイルを読み込んで、中身のファイルを読み込む
	 * 処理内で@Deprecatedがある処理が使われている
	 * @param path ファイルパス
	 */
	private static void readTxtInZip_deprecated(String path) {
		// このZipFile(コンストラクタ)がDeprecatedになった
		// https://commons.apache.org/proper/commons-compress/apidocs/org/apache/commons/compress/archivers/zip/ZipFile.html#%3Cinit%3E(java.lang.String)
		File file = new File(path.toString()); 
		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

			while (entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					try (InputStream stream = zipFile.getInputStream(entry);
							BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
						String line;
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * zipファイルを読み込んで、中身のファイルを読み込む
	 * コンパイルの警告が出ないように修正を行った
	 * @param path ファイルパス
	 */
	private static void readTxtInZip_new(String path) {
		// Use ZipFile.Builder.get()とのことなので、
		// ZipFile(String path) -> ZipFile.builder().setFile(File file).get()
		// と差し替えることで等価な処理になる
		// 参考：https://www.mail-archive.com/commits@commons.apache.org/msg113948.html
		File file = new File(path.toString()); 
		try (ZipFile zipFile = ZipFile.builder().setFile(file).get()) {
			Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

			while (entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					try (InputStream stream = zipFile.getInputStream(entry);
							BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
						String line;
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 読み取り対象のzipファイルパスを取得
	 */
	private static String getTargetPath() {
		// 対象のzipファイルの相対パス（Main.javaから見た）
		String zipRelativePath = "../resources/some.zip";

		// システムプロパティ "java.class.path" からクラスパスを取得
		String classPath = System.getProperty("java.class.path");

		// クラスパスの最初のエントリを取得（クラスパスはセミコロンまたはコロンで区切られている）
		String[] classPathEntries = classPath.split(System.getProperty("path.separator"));

		String firstEntry = classPathEntries[0];

		Path basePath = Paths.get(firstEntry).toAbsolutePath().getParent();

		// 基本パスに相対パスを結合
		String filePath = basePath.resolve(zipRelativePath).toString();

		return filePath;
	}

}
