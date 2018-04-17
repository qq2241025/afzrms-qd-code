package test.com.mapabc.gater.directl.parse;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.ParsePND;

public class ParsePNDTest {
	ParsePND parsePND = new ParsePND();

	@Test
	public void testParseSingleGprs() throws UnsupportedEncodingException {
		byte[] moBytes = "WZTREQ,610,354525045423835,150531,125529,36.2428452491421,116.23476575211109,91.09,88,229,3,1=0.10000000149011612!2=0|3,01,0,0#,size=2"
				.getBytes("utf-8");
		ParseBase parseSingleGprs = parsePND.parseSingleGprs(moBytes);
	}

}
