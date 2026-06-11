package se.fk.github.portaladminbff.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class OulSorteringsordningEntry
{
   public List<Map<String, Object>> constraints;

   @JsonProperty("sort_by")
   public OulSortBy sortBy;
}
