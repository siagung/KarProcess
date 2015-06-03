package com.drmattyg.nanokaraoke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;


public class MidiFile implements Iterable<TrackChunk>{	
	
	public static class VarLength {
		public int value; // the value of the var length field
		public int size; // the size in bytes of the var length field
		
		
		// lessons from the bug I fixed below:
		// 1: Copying over the minimal data structure you need is worth the overhead probably; passing around the full byte array
		// invites the kinds of bugs I encountered.  Copying into an immutable array improves safety, and also allows me to trim the amount
		// of data that's being accessed.  I wouldn't have had this issue
		// 2: Basic unit tests here on these functions of course would have caught this.  Bug got introduced when I refactored
		// 3: The resuse of the term "i + offset", even once, should be a tipoff: don't do that.  Create a new variable.  That would have fixed the the error probably, 
		// assuming we didn't do #1, which we should have
		public static VarLength read(byte[] b, int offset) {
			VarLength v = new VarLength();
			v.value = 0;
			for(int i = 0; i < b.length; i++) {
				v.value = (v.value << 7) + (b[i + offset] & 0x7F);
				if((b[i + offset] & 0x80) == 0) {
					v.size = i + 1;
					break;
				}
			}
			return v;
		}
	}
	
	byte[] bytes;
	int offset;
	HeaderChunk header;
	private MidiFile(String s) {
		Path path = Paths.get(s);
		try {
			bytes = Files.readAllBytes(path);
			header = HeaderChunk.getInstance(this);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	public static MidiFile createInstance(String s) {
		return new MidiFile(s);
	}
	
	public byte[] getBytes() { return bytes; } // for testing

	
	public HeaderChunk getHeaderChunk() { return HeaderChunk.getInstance(this); }

	private int iteratorOffset;
	
	@Override
	public Iterator<TrackChunk> iterator() {
		iteratorOffset = header.getTotalLength(); // this is always the header length
		final MidiFile mf = this;
		return new Iterator<TrackChunk>() {

			@Override
			public boolean hasNext() {
				
				return iteratorOffset < bytes.length && TrackChunk.isTrackChunk(mf, iteratorOffset);
			}

			@Override
			public TrackChunk next() {
				TrackChunk tc = TrackChunk.getInstance(mf, iteratorOffset);
				iteratorOffset += tc.getTotalLength();
				return tc;
			}

			@Override
			public void remove() {
				// not implemented
				
			}
			
		};
	}
	
}
