package com.beawy.Bandcoffee;

import javax.swing.*;

public class Main {
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		BandcampDownloader.download();
	}
}
