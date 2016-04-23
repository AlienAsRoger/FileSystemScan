package com.example.roger.fileexplorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.roger.fileexplorer.R;
import com.example.roger.fileexplorer.utils.FileInfo;
import com.example.roger.fileexplorer.utils.Utils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 11:45
 */
public class MyAdapter extends ArrayAdapter<FileInfo> {

	public MyAdapter(Context context, int resource, List<FileInfo> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
		}
		TextView fileNameTxt = (TextView) convertView.findViewById(R.id.fileNameTxt);
		TextView fileSizeTxt = (TextView) convertView.findViewById(R.id.fileSizeTxt);
		TextView filePathTxt = (TextView) convertView.findViewById(R.id.filePathTxt);

		FileInfo fileInfo = getItem(position);

		filePathTxt.setText(fileInfo.getPath());
		fileSizeTxt.setText(Utils.convertBytes(fileInfo.getSize()));
		fileNameTxt.setText(fileInfo.getName());

		return convertView;
	}


}
