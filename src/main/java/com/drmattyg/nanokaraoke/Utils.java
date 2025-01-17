package com.drmattyg.nanokaraoke;

import java.util.Arrays;

import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;

public class Utils {
	private static final IRational millis = IRational.make(1, 1000);
	public static int toInt(byte[] b, int offset) {
		return (b[offset]<<24)&0xff000000|
	       (b[offset+1]<<16)&0x00ff0000|
	       (b[offset+2]<< 8)&0x0000ff00|
	       (b[offset+3]<< 0)&0x000000ff;
	}
	
	public static int toInt(byte[] b, int length, int offset) {
		int v = b[offset] & 0xFF;
		for(int i = offset + 1; i < length + offset; i++) {
			v = (v << 8) + (b[i] & 0xFF);
		}
		return v;

	}
	
	public static String byteToString(byte[] b, int size, int offset) {
		return new String(Arrays.copyOfRange(b, offset, offset + size));
	}
	
	// TODO: this only works for tracks with a single tempo; need to fix this
	public static int deltaToMillis(int tempo, int division, long d) {
//		double bpm = 1/(tempo/1e6/60);
		return Math.round(d*tempo*1.0f/(division * 1000.0f));
	}
	
	public static long getVideoLength(String filename) throws IllegalArgumentException {
		
		IContainer c = IContainer.make();
		if(c.open(filename, IContainer.Type.READ, null) < 0) {
			throw new IllegalArgumentException("Unable to open " + filename);
		}
		for(int i = 0; i < c.getNumStreams(); i++) {
			if(c.getStream(i).getStreamCoder().getCodecType() == Type.CODEC_TYPE_VIDEO) {
				IStream s = c.getStream(i);
				IRational tb = s.getTimeBase();
				return millis.rescale(s.getDuration(), s.getTimeBase());
			}
		}
		return -1;
	}
	

}
