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

import com.beust.jcommander.JCommander;
import org.junit.Assert;
import org.junit.Test;

public class OptionsTest {
  @Test
  public void testOptions() {
    String[] args = {"-pr", "/Users/mpathira/"};
    Options options = new Options();
    new JCommander(options, args);

    Assert.assertEquals(options.pairTreeRoot, "/Users/mpathira/");
  }

  @Test
  public void testMissingOptions() {
    String[] args = {};
    Options options = new Options();

    try {
      new JCommander(options, args);
    } catch (Exception e) {
      Assert.assertTrue(e.getMessage().contains("is required: --pairtree-root, -pr"));
    }
  }
}
