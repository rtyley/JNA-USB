package com.madgag.garmin;

import java.util.List;

import com.madgag.garmin.GarminUsbDevice.ReadResult;
import com.madgag.simpleusb.Bits;

public class ProductRequestProtocol {
	public void getA000andA001(GarminUsbDevice garminDevice) {
		garminDevice.write(GarminPacket.getProductRequestPacket());

		final short L000_Pid_Protocol_Array = 0x00fd, L000_Pid_Product_Rqst = 0x00fe, L000_Pid_Product_Data = 0x00ff, L000_Pid_Ext_Product_Data = 0x00f8;

		ReadResult read;
		boolean done = false;
		while (!done
				&& (read = garminDevice.read()).getStatus().getTransferred() > 0) {
			System.out.println("Reading...");
			GarminPacket packet = read.getPacket();
			byte[] data = packet.getData();
			switch (packet.getId()) {
			case L000_Pid_Product_Data:
				short productId = Bits.getShortL(data, 0); // unsigned
				short softwareVersion = Bits.getShortL(data, 2); // signed...
				String productDescription = new String(data, 4, data.length - 4);
				System.out.println("productId=" + productId
						+ " softwareVersion=" + softwareVersion
						+ " productDescription=" + productDescription);
				break;
			case L000_Pid_Ext_Product_Data:
				String extData = new String(data);
				System.out.println("Extra prod data - ignore! : " + extData);
				break;
			case L000_Pid_Protocol_Array:
				List<ProtocolDataTag> pds = ProtocolDataTag
						.fromProtocolArrayData(data);
				DeviceProtocols deviceProtocols = DeviceProtocols.from(pds);
				done = true;
				System.out.println(pds);
				System.out.println(deviceProtocols);
				break;
			default:
				System.out.println("Ignoring: " + packet);
			}
		}
	}
}
