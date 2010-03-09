package com.madgag.simpleusb;

import static com.madgag.simpleusb.UsbEndpointDirection.IN;
import static com.madgag.simpleusb.UsbEndpointDirection.OUT;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class UsbEndpointDirectionTest {

	@Test
	public void shouldDecode() throws Exception {
		assertThat(UsbEndpointDirection.from((byte)0x80),equalTo(IN));
		assertThat(UsbEndpointDirection.from((byte)0x00),equalTo(OUT));
	}
	
}
