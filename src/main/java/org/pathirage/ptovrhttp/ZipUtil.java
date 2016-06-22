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

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
  public static File zip(List<String> srcFiles) throws IOException {
    // create byte buffer
    byte[] buffer = new byte[1024];
    File temp = Files.createTempFile("volumes-", ".zip").toFile();
    FileOutputStream fos = new FileOutputStream(temp);
    ZipOutputStream zos = new ZipOutputStream(fos);

    for (String f : srcFiles) {
      File srcFile = new File(f);
      FileInputStream fis = new FileInputStream(srcFile);
      // begin writing a new ZIP entry, positions the stream to the start of the entry data
      zos.putNextEntry(new ZipEntry(srcFile.getName()));
      int length;
      while ((length = fis.read(buffer)) > 0) {
        zos.write(buffer, 0, length);
      }
      zos.closeEntry();
      // close the InputStream
      fis.close();
    }

    // close the ZipOutputStream
    zos.close();
    return temp;

  }
}
