package se.fk.github.portaladminbff;

import se.fk.github.portaladminbff.model.*;

public class UppgiftMapper
{
   public static OperativUppgift transform(RawOperativUppgift raw)
   {
      OperativUppgift result = new OperativUppgift();
      result.uppgiftId = raw.uppgiftId;
      result.handlaggningId = raw.handlaggningId;
      result.skapad = raw.skapad;
      result.status = raw.status;
      result.handlaggarId = raw.handlaggarId;
      result.planeradTill = raw.planeradTill != null ? raw.planeradTill : "";
      result.utford = raw.utford != null ? raw.utford : "";
      result.individer = raw.individer;
      result.regel = raw.regel;
      result.beskrivning = raw.beskrivning;
      result.verksamhetslogik = raw.verksamhetslogik;
      result.roll = raw.roll;
      result.url = raw.url;
      return result;
   }

   public static OulUpdateUppgiftRequest toOulRequest(UpdateUppgiftRequest request)
   {
      OulUpdateUppgiftRequest oul = new OulUpdateUppgiftRequest();
      if (request.handlaggarId != null)
      {
         OulIdtyp id = new OulIdtyp();
         id.typId = request.handlaggarId.typId;
         id.varde = request.handlaggarId.varde;
         oul.handlaggarId = id;
      }
      return oul;
   }
}
