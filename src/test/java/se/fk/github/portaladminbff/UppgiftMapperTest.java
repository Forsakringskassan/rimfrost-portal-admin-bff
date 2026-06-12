package se.fk.github.portaladminbff;

import org.junit.jupiter.api.Test;
import se.fk.github.portaladminbff.model.HandlaggarId;
import se.fk.github.portaladminbff.model.OperativUppgift;
import se.fk.github.portaladminbff.model.OulUpdateUppgiftRequest;
import se.fk.github.portaladminbff.model.RawOperativUppgift;
import se.fk.github.portaladminbff.model.UpdateUppgiftRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UppgiftMapperTest
{
   @Test
   void transform_mapsAllFields()
   {
      RawOperativUppgift raw = new RawOperativUppgift();
      raw.uppgiftId = "test-001";
      raw.handlaggningId = "h-001";
      raw.skapad = "2025-01-10T08:00:00Z";
      raw.status = "Ny";
      raw.regel = "RTF_MANUELL";
      raw.roll = "Handläggning";
      raw.beskrivning = "Testbeskrivning";
      raw.verksamhetslogik = "VAB";
      raw.url = "http://example.com";
      raw.planeradTill = "2025-02-01";
      raw.utford = "2025-01-15";
      raw.individer = List.of();

      OperativUppgift result = UppgiftMapper.transform(raw);

      assertEquals("test-001", result.uppgiftId);
      assertEquals("h-001", result.handlaggningId);
      assertEquals("2025-01-10T08:00:00Z", result.skapad);
      assertEquals("Ny", result.status);
      assertEquals("RTF_MANUELL", result.regel);
      assertEquals("Handläggning", result.roll);
      assertEquals("2025-02-01", result.planeradTill);
      assertEquals("2025-01-15", result.utford);
   }

   @Test
   void transform_mapsHandlaggarId()
   {
      HandlaggarId hid = new HandlaggarId();
      hid.typId = "abc123";
      hid.varde = "19901010-1234";

      RawOperativUppgift raw = minimalRaw();
      raw.handlaggarId = hid;

      OperativUppgift result = UppgiftMapper.transform(raw);

      assertNotNull(result.handlaggarId);
      assertEquals("abc123", result.handlaggarId.typId);
      assertEquals("19901010-1234", result.handlaggarId.varde);
   }

   @Test
   void transform_handlesNullHandlaggarId()
   {
      RawOperativUppgift raw = minimalRaw();
      raw.handlaggarId = null;

      OperativUppgift result = UppgiftMapper.transform(raw);

      assertNull(result.handlaggarId);
   }

   @Test
   void transform_replacesNullPlaneradTillWithEmptyString()
   {
      RawOperativUppgift raw = minimalRaw();
      raw.planeradTill = null;

      OperativUppgift result = UppgiftMapper.transform(raw);

      assertEquals("", result.planeradTill);
   }

   @Test
   void transform_replacesNullUtfordWithEmptyString()
   {
      RawOperativUppgift raw = minimalRaw();
      raw.utford = null;

      OperativUppgift result = UppgiftMapper.transform(raw);

      assertEquals("", result.utford);
   }

   @Test
   void toOulRequest_mapsHandlaggarId()
   {
      HandlaggarId hid = new HandlaggarId();
      hid.typId = "kortnummer";
      hid.varde = "12345";

      UpdateUppgiftRequest request = new UpdateUppgiftRequest();
      request.handlaggarId = hid;

      OulUpdateUppgiftRequest result = UppgiftMapper.toOulRequest(request);

      assertNotNull(result.handlaggarId);
      assertEquals("kortnummer", result.handlaggarId.typId);
      assertEquals("12345", result.handlaggarId.varde);
   }

   @Test
   void toOulRequest_handlesNullHandlaggarId()
   {
      UpdateUppgiftRequest request = new UpdateUppgiftRequest();
      request.handlaggarId = null;

      OulUpdateUppgiftRequest result = UppgiftMapper.toOulRequest(request);

      assertNull(result.handlaggarId);
   }

   private RawOperativUppgift minimalRaw()
   {
      RawOperativUppgift raw = new RawOperativUppgift();
      raw.uppgiftId = "x";
      raw.handlaggningId = "x";
      raw.skapad = "2025-01-01";
      raw.status = "Ny";
      raw.regel = "RTF";
      raw.roll = "R";
      raw.beskrivning = "b";
      raw.verksamhetslogik = "v";
      raw.url = "";
      raw.individer = List.of();
      return raw;
   }
}
