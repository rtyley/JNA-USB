package com.madgag.garmin;

import static com.madgag.garmin.GarminPacketFactory.A010_Cmnd_Start_Pvt_Data;
import static java.lang.Math.round;
import static java.nio.ByteBuffer.wrap;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import com.madgag.garmin.GarminUsbDevice.ReadResult;
import com.madgag.garmin.GarminUsbDevice.TransferResultStatus;

public class PositionVelocityTimeProtocol {
	private static short L001_Pid_Pvt_Data = 0x0033;
	
	public void getPVT(GarminUsbDevice garminDevice) throws Exception {

		TransferResultStatus status = garminDevice.write(GarminPacketFactory
				.getL001CommandDataPacket(A010_Cmnd_Start_Pvt_Data));
		while (true) {
			ReadResult read = garminDevice.read();
			GarminPacket packet = read.getPacket();
			if (packet.getId() == L001_Pid_Pvt_Data) {
				unpackD800(packet.getData());
			}
		}

	}

	/*
	 * typedef struct { float32 float32 float32 float32 uint16 float64
	 * radian_position_type float32 float32 float32 float32 sint16 uint32 }
	 * D800_Pvt_Data_Type;
	 */

	private static void unpackD800(byte[] data) throws IOException {
		DateTime systemNow=new DateTime();
		ByteBuffer buffer = wrap(data).order(LITTLE_ENDIAN);
		float alt = buffer.getFloat();
		float epe = buffer.getFloat();
		float eph = buffer.getFloat();
		float epv = buffer.getFloat();
		int fix = buffer.getShort();
		double tow = buffer.getDouble();
		Duration towDuration = new Duration(round(1000*tow));
		
		BasicDataTypes.Coord.fromRadianPositionType(buffer);
		
		float east = buffer.getFloat();
		float north = buffer.getFloat();
		float up = buffer.getFloat();
		float msl_hght = buffer.getFloat();
		short leap_scnds = buffer.getShort();
		Duration leapSeconds=Duration.standardSeconds(leap_scnds);
		System.out.println(fix+" "+tow+" "+leap_scnds);
		
		int wn_days = buffer.getInt();
		Period daysFromStartOfEpochToStartOfWeek = Period.days(wn_days);
		
		DateTime utcNow = BasicDataTypes.EPOCH.plus(daysFromStartOfEpochToStartOfWeek).plus(towDuration).minus(leapSeconds);
		System.out.println(utcNow + " "+new Duration(utcNow, systemNow));
	}
	
	/*
static void
garmin_unpack_d800 ( D800 * pvt, uint8 ** pos )
{
  GETF32(pvt->alt);
  GETF32(pvt->epe);
  GETF32(pvt->eph);
  GETF32(pvt->epv);
  GETU16(pvt->fix);
  GETF64(pvt->tow);
  
  GETRPT(pvt->posn);
  GETF32(pvt->east);
  GETF32(pvt->north);
  GETF32(pvt->up);
  GETF32(pvt->msl_hght);
  GETS16(pvt->leap_scnds);
  GETU32(pvt->wn_days);
}

	 */
}
