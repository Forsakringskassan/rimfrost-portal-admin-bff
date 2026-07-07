package se.fk.github.portaladminbff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OulSorteringsordningEntry
{
   public List<Map<String, Object>> constraints;

   @JsonProperty("sort_by")
   public OulSortBy sortBy;
}
