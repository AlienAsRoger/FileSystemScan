package com.example.roger.fileexplorer.utils;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 11:18
 */
public class FileInfo {
	String path;
	String name;
	private boolean isDir;
	long size;

	public FileInfo() {

	}

	public FileInfo(long size, String path, boolean isDir) {
		this.size = size;
		this.path = path;
		this.isDir = isDir;

		name = Utils.getFilenameFromPath(path);
	}

	public String getPath() {
		return path;
	}


	public long getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean dir) {
		isDir = dir;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
