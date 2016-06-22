/**
 * Copyright 2016 Milinda Pathirage
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pathirage.ptovrhttp;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class JSONSerdeTest {
  @Test
  public void testRequestSerde() {
    VolumesRequest vr = new VolumesRequest();

    ArrayList<String> volumes = new ArrayList<>();
    volumes.add("miua.5252888,0001,001");
    volumes.add("miua.5252814,0001,001");
    volumes.add("miua.5252798,0004,002");

    vr.setVolumes(volumes);

    Gson gson = new Gson();
    String vrJson = gson.toJson(vr);

    System.out.println(vrJson);

    Assert.assertTrue(vrJson.contains("volumes"));

    vr = gson.fromJson(vrJson, VolumesRequest.class);

    Assert.assertTrue(vr.getVolumes().contains("miua.5252798,0004,002"));
  }
}
