package se.fk.github.portaladminbff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TasksResponse
{
   @JsonProperty("operativa_uppgifter")
   public List<OperativUppgift> operativaUppgifter;
}
