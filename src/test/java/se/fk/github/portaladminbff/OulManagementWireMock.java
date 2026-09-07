package se.fk.github.portaladminbff;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

public class OulManagementWireMock implements QuarkusTestResourceLifecycleManager
{
   public static WireMockServer server;

   @Override
   public Map<String, String> start()
   {
      server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
      server.start();
      return Map.of(
            "quarkus.rest-client.oul-management.url", server.baseUrl());
   }

   @Override
   public void stop()
   {
      if (server != null && server.isRunning())
      {
         server.stop();
      }
   }
}
