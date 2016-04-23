package com.example.roger.fileexplorer.interfaces;

import com.example.roger.fileexplorer.utils.FileInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 10:11
 */
public interface ListenerInterface {

	void showProgress(boolean show);

	void onUpdate(List<FileInfo> obj);
}
