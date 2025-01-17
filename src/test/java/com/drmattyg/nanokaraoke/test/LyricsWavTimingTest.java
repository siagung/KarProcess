package com.drmattyg.nanokaraoke.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.drmattyg.nanokaraoke.MidiEventHandlers;
import com.drmattyg.nanokaraoke.MidiFile;
import com.drmattyg.nanokaraoke.TextEvent;
import com.drmattyg.nanokaraoke.Utils;
import com.drmattyg.nanokaraoke.convert.ProcessKar;
import java.io.*;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.*;

import junit.framework.TestCase;

public class LyricsWavTimingTest extends TestCase {

//	private static final String MIDI_FILE = "/Users/mgordon/Downloads/all_shook_up_karaoke_songs_NifterDotCom.kar";
//	private static final String MIDI_FILE = "src/test/resources/bridge_over_troubled_water.kar"; WORKS start delta = 0
//	private static final String MIDI_FILE = "/Users/mgordon/Downloads/crazy_little_thing_called_love_karaoke_songs_NifterDotCom.kar";
//	private static final String MIDI_FILE = "//Users/mgordon/test/Black velvet - Alannah Miles.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/test/Eurythmics - Sweet Dreams.kar";
//	private static final String MIDI_FILE = "/Users/mgordon/test/karaoke/karfile/Peaceful easy feeling - The Eagles.kar";
	private static final String MIDI_FILE = "/Users/mgordon/test/karaoke/karfile/Have you ever seen the rain - Credence Clearwater Revival.kar";

	@Test
	public void test() {
		MidiFile mf = MidiFile.getInstance(MIDI_FILE);
		List<Integer> deltas = mf.getTextHandler().getSortedTimeOffsets();
		Map<Integer, TextEvent> events = mf.getTextHandler().getTextMap();
		try {
			File wf = ProcessKar.karToWav(MIDI_FILE);
			AudioInputStream stream = AudioSystem.getAudioInputStream(wf);
			AudioFormat format = stream.getFormat();
		    DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
			System.out.println("Starting");
			long startTime = System.currentTimeMillis();
			int startDelta = mf.getMusicStartDelta();
			startDelta = 0;
			System.out.println("Start delta = " + startDelta);
			long timeOffset = mf.timeOffsetForDelta(startDelta);
			System.out.println("Time offset: " + timeOffset);
			for(Integer d : deltas) {

//				long t = Utils.deltaToMillis(tempo, div, d - startDelta);
				long t = mf.timeOffsetForDelta(d) - timeOffset;
//				System.out.println(t);
				long currentTime;
//				while(System.currentTimeMillis()  - startTime < t) {
				while((currentTime = System.currentTimeMillis()  - startTime) < t) {
//					System.out.println(currentTime);
//					System.out.println("Current time: " + currentTime + " : t: " + t);
					Thread.sleep(50);
				}
				System.out.println(events.get(d) + " : " + t);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
			
		}
	}
}
