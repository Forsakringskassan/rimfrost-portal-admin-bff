package se.fk.github.portaladminbff.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HandlaggarId
{
   @JsonProperty("typId")
   @JsonAlias("typ_id")
   public String typId;

   @JsonProperty("varde")
   public String varde;
}
