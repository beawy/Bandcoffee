package com.beawy.Bandcoffee;

import com.eclipsesource.json.JsonObject;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BandcampDownloader {

  public static void download(String url, String path) throws java.io.IOException {
	//Set up document
	Document d = Jsoup.connect(url).get();
	Pattern p = Pattern.compile(".*TralbumData = \\{(.*?)\\};.*", Pattern.DOTALL);
	Matcher m = p.matcher(d.select("script").html());

	//Get album data
	String albumName = d.select("h2[class=trackTitle][itemprop=name]").first().text();
	String imageURL = d.select("a[class=popupImage] > img[itemprop=image]").first().attr("src");

	String outDir = path + "/" + getValidPathName(albumName);
	new File(outDir).mkdir();

	FileOutputStream fos = new FileOutputStream(outDir + "/cover" + (imageURL.contains(".jpg") ? ".jpg" : ".png"));
	fos.getChannel().transferFrom(java.nio.channels.Channels.newChannel(new URL(imageURL).openStream()), 0L, Long.MAX_VALUE);
	fos.close();

	//Download tracks
	JsonObject json = JsonObject.readFrom(m
			.replaceFirst("{$1}")
			.replaceAll("\\/\\/(?!.*?\\\").*?\\n", "")
			.replace("\" + \"", "")
			.replace("            ", "")			//annoying regex shit cuz Minimal-Json ain't woke enough to handle this shit alone
			.replaceAll("    (.*?):", "\"$1\":"));
	JsonObject crt = json.get("current").asObject();

	String album = crt.get("title").asString();
	String artist = crt.get("artist").isNull() ? "" : crt.get("artist").asString();
	String release = crt.get("release_date").asString().replaceAll(".*?([0-9]{4}).*", "$1");

	System.out.println(album + " (" + artist + ", " + release + ")");

	//ProgressBar
	JProgressBar progressBar = new JProgressBar(0, json.get("trackinfo").asArray().size());
	progressBar.setValue(0);
	progressBar.setStringPainted(true);

	(new Thread(){
	  @Override
	  public void run() {
		JOptionPane.showMessageDialog(null, new Object[]{progressBar}, "Progress", JOptionPane.PLAIN_MESSAGE);//null, new Object[]{progressBar, new JScrollPane(progressOutput)}, "Progress", JOptionPane.PLAIN_MESSAGE);
	  }
	}).start();

	//Download Each Track
	json.get("trackinfo").asArray().forEach(e -> {
	  JsonObject eo = e.asObject();

	  try {
		String track = String.format("%02d", eo.get("track_num").asInt());
		String title = eo.get("title").asString();
		String fn = outDir + "/" + getValidPathName((track + " - " + title)) + ".mp3";

		System.out.println(track + " - " + title);

		if(!eo.get("file").isNull()) {
		  FileOutputStream foss = new FileOutputStream(fn);
		  foss.getChannel().transferFrom(java.nio.channels.Channels.newChannel(new URL(eo.get("file").asObject().get("mp3-128").asString()).openStream()), 0L, Long.MAX_VALUE);
		  foss.close();

		  //Set MP3 Tags
		  Mp3File mp3File = new Mp3File(fn);
		  ID3v24Tag tag = new ID3v24Tag();

		  tag.setTitle(title);
		  tag.setTrack(track);
		  tag.setAlbum(album);
		  tag.setArtist(artist);
		  tag.setYear(release);

		  tag.setAlbumImage(Files.readAllBytes(Paths.get(outDir + "/cover.jpg")), "cover");

		  mp3File.setId3v2Tag(tag);

		  mp3File.save(fn + ".new");
		  if (new File(fn).delete())
			new File(fn + ".new").renameTo(new File(fn));
		}
	  } catch (Exception e1) {
		e1.printStackTrace();
	  }

	  progressBar.setValue(progressBar.getValue()+1);
	});
  }

  public static void download() {
	JTextField url = new JTextField();
	final JTextField dir = new JTextField(System.getProperty("user.home") + "\\desktop");
	JButton selectDir = new JButton("Select Dir");

	dir.setEnabled(false);
	selectDir.addActionListener(e -> {
	  JFileChooser f = new JFileChooser(dir.getText());
	  f.setFileSelectionMode(1);
	  f.setMultiSelectionEnabled(false);

	  if (f.showOpenDialog(null) == 0) {
		dir.setText(f.getSelectedFile().getAbsolutePath());
	  }

	});
	Object[] m = {"URL:", url, "Dir:", dir, selectDir};


	if (JOptionPane.showConfirmDialog(null, m, "", 2) == 0) {
	  try {
		//JOptionPane.showMessageDialog(fr, "Just wait a bit while we \"legally\" download that jazz for ya!\nAfter shit's done you'll see another message-dialog-thingy");
		download(url.getText(), dir.getText());
	  } catch (Exception e) {
		JOptionPane.showMessageDialog(null, e.getMessage());
		System.err.println(e);
	  }
	}
  }

  private static String getValidPathName(String s) {
	return s.replace("<", "[").replace(">", "]").replace(":", "-").replace("*", "-").replace("?", "").replace("\"", " ").replace("/", " ").replace("\\", " ").replace("|", " ");
  }
}
