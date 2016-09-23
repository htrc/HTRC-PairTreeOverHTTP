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
import com.google.common.base.Joiner;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import gov.loc.repository.pairtree.Pairtree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
  private static Logger log = LoggerFactory.getLogger(Main.class);

  private static Gson gson = new Gson();
  private static Pairtree pairtree = new Pairtree();

  public static void main(String[] args) {
    Options options = new Options();
    new JCommander(options, args);

    log.info("Starting PairTreeOverHTTP with pairtree root: " + options.pairTreeRoot);

    port(8080);
    post("/volumes", "application/json", (request, response) -> {
      List<String> volumeZips = new ArrayList<String>();
      VolumesRequest requestedVolumes = gson.fromJson(request.body(), VolumesRequest.class);
      for (String vol : requestedVolumes.getVolumes()) {
        String[] volSplits = vol.split("\\.");
        String volId = pairtree.cleanId(volSplits[1]);
        String library = volSplits[0].trim();

        if (volSplits.length > 2) {
          response.status(400);
          return "Malformed volume id " + vol;
        }

        List<String> volIdSplits = new ArrayList<String>();

        for (int i = 0; i < volId.length(); i += 2) {
          volIdSplits.add(volId.substring(i, i + 2));
        }

        volumeZips.add(String.format("%s/%s/pairtree_root/%s/%s/%s.zip", options.pairTreeRoot, library, Joiner.on("/").join(volIdSplits), volId, volId));
      }
      File zip = ZipUtil.zip(volumeZips);
      response.header("Content-Disposition", String.format("attachment; filename=\"%s\"", zip.getName()));
      response.type(MediaType.ZIP.toString());
      response.raw().setContentLength((int)zip.length());
      response.status(200);

      OutputStream out = response.raw().getOutputStream();
      InputStream in = new FileInputStream(zip);
      IOUtils.copy(in, out);
      out.close();
      in.close();
      return null;
    });
  }
}
