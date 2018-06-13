# Bandcoffee
A Java-based Bandcamp album downloader applet I made that lets you download Bandcamp (https://bandcamp.com/) releases for free. See description for more info. It only will work on tracks that have a play/stream option on Bandcamp though.

## Installation
To install this application either head over to [the newest version](https://github.com/beawy/Bandcoffee/releases/latest) and download the binary *(the .jar file)* or download/clone this project and execute the ``mvn clean compile assembly:single`` command (assuming you have maven).

## Usage
Just execute the .jar file *(if it doesn't work make sure you have Java installed, ffs otherwise why are you on GitHub looking for this stuff duh :P)*, paste the URL of the album you wanna download and specify where it sould be downloaded to *(a folder for the album will be generated in the directory you specified)*.
After pressing the OK button, you'll be shown a small window showing the current progress.
You can close this window if you want to since the download will continue either way.
After the download is done you'll find a cover image as well as the tracks *(with proper title and mp3 tags)* in the generated directory.

## Disclaimer
In the event you think I'm a psychopath and you're gonna go to jail for using this, please check out what Bandcamp has to say [Bandcamps position on this matter](https://bandcamp.com/help/audio_basics#steal) as well as their [general terms of use](https://bandcamp.com/terms_of_use) which should clarify that this application is not some shady bs that will get you sued by FuckShit (https://fuckshit.bandcamp.com/). :)
