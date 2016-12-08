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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static spark.Spark.*;

public class Main {
  private static Logger log = LoggerFactory.getLogger(Main.class);

  private static Gson gson = new Gson();
  private static Pairtree pairtree = new Pairtree();

  public static void main(String[] args) {
    Options options = new Options();
    new JCommander(options, args);

    log.info("Starting PairTreeOverHTTP with pairtree root: " + options.pairTreeRoot);

    port(options.port);
    post("/volumes", "application/json", (request, response) -> {
      try {
        List<String> volumeZips = new ArrayList<String>();
        VolumesRequest requestedVolumes = gson.fromJson(request.body(), VolumesRequest.class);
        for (String vol : requestedVolumes.getVolumes()) {
          int indexOfFirstPeriod = vol.indexOf('.');

          String volId;
          if (!requestedVolumes.isClean()) {
            volId = pairtree.cleanId(vol.substring(indexOfFirstPeriod + 1));
          } else {
            volId = vol.substring(indexOfFirstPeriod + 1);
          }

          String library = vol.substring(0, indexOfFirstPeriod);
          volumeZips.add(String.format("%s/%s/pairtree_root/%s/%s/%s.zip", options.pairTreeRoot, library, Joiner.on("/").join(volId.split("(?<=\\G.{2})")), volId, volId));
        }

        response.header("Content-Disposition", String.format("attachment; filename=\"%s\"", "volumes.zip"));
        response.type(MediaType.ZIP.toString());
        response.status(200);

        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.raw().getOutputStream()));
        zos.setMethod(ZipOutputStream.DEFLATED);
        zos.setLevel(0);
        byte[] buffer = new byte[1024];

        for (String f : volumeZips) {
          File srcFile = new File(f);
          try (BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f))) {
            // begin writing a new ZIP entry, positions the stream to the start of the entry data
            zos.putNextEntry(new ZipEntry(srcFile.getName()));
            int length;
            while ((length = fis.read(buffer)) > 0) {
              zos.write(buffer, 0, length);
            }
            zos.closeEntry();
          }
        }

        zos.flush();
        zos.close();
      } catch (Exception e) {
        log.error("Cannot create zip.", e);
        halt(405, "Server Error: " + e.getMessage());
      }

      return response.raw();
    });
  }
}
