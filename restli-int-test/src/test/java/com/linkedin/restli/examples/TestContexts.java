/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.restli.examples;

import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.FindRequest;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Tone;
import com.linkedin.restli.examples.greetings.client.WithContextBuilders;
import junit.framework.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

/**
 * @author Moira Tagle
 * @version $Revision: $
 */

public class TestContexts extends RestLiIntegrationTest
{

  private static final Client CLIENT = new TransportClientAdapter(new HttpClientFactory().getClient(Collections.<String, String>emptyMap()));
  private static final String URI_PREFIX = "http://localhost:1338/";
  private static final RestClient REST_CLIENT = new RestClient(CLIENT, URI_PREFIX);
  private static final WithContextBuilders WITH_CONTEXT_BUILDERS = new WithContextBuilders();

  private static final String PROJECTION_MESSAGE = "Projection!";
  private static final String NO_PROJECTION_MESSAGE = "No Projection!";
  private static final Tone HAS_KEYS = Tone.FRIENDLY;
  private static final Tone NO_KEYS = Tone.INSULTING;

  @BeforeClass
  public void initClass() throws Exception
  {
    super.init();
  }

  @AfterClass
  public void shutDown() throws Exception
  {
    super.shutdown();
  }

  @Test
  public void testGet() throws RemoteInvocationException
  {
    GetRequest<Greeting> requestWithProjection =
      WITH_CONTEXT_BUILDERS.get().id(5L).fields(Greeting.fields().message(), Greeting.fields().tone()).build();
    Greeting projectionGreeting = REST_CLIENT.sendRequest(requestWithProjection).getResponse().getEntity();
    Assert.assertEquals(projectionGreeting.getMessage(), PROJECTION_MESSAGE);
    Assert.assertEquals(projectionGreeting.getTone(), HAS_KEYS);

    GetRequest<Greeting> requestNoProjection = WITH_CONTEXT_BUILDERS.get().id(5L).build();
    Greeting greeting = REST_CLIENT.sendRequest(requestNoProjection).getResponse().getEntity();
    Assert.assertEquals(greeting.getMessage(), NO_PROJECTION_MESSAGE);
    Assert.assertEquals(greeting.getTone(), HAS_KEYS);
  }

  @Test
  public void testFinder() throws RemoteInvocationException
  {
    FindRequest<Greeting> requestWithProjection =
      WITH_CONTEXT_BUILDERS.findByFinder().fields(Greeting.fields().message(), Greeting.fields().tone()).build();
    List<Greeting> projectionGreetings = REST_CLIENT.sendRequest(requestWithProjection).getResponse().getEntity().getElements();

    Greeting projectionGreeting = projectionGreetings.get(0);
    Assert.assertEquals(projectionGreeting.getMessage(), PROJECTION_MESSAGE);
    Assert.assertEquals(projectionGreeting.getTone(), NO_KEYS);

    FindRequest<Greeting> requestNoProjection = WITH_CONTEXT_BUILDERS.findByFinder().build();
    List<Greeting> noProjectionGreetings = REST_CLIENT.sendRequest(requestNoProjection).getResponse().getEntity().getElements();

    Greeting noProjectionGreeting = noProjectionGreetings.get(0);
    Assert.assertEquals(noProjectionGreeting.getMessage(), NO_PROJECTION_MESSAGE);
    Assert.assertEquals(noProjectionGreeting.getTone(), NO_KEYS);
  }
}
