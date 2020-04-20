/*
 * The MIT License
 *
 * Copyright 2020 Emre Demir.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.redelsoft.gisconverter.core;

import java.io.File;
import net.redelsoft.gisconverter.ext.ExtensionsUtil;
import net.redelsoft.gisconverter.ext.FileExtension;
import org.apache.commons.io.FilenameUtils;
import org.geotools.data.FeatureSource;

/**
 * GIS Converter File Processor
 * <p>
 * Execute the processor after settings file and file extension parameters.
 *
 * @author Emre Demir
 */
public class ConvertProcess {

    private File file;
    private FileExtension ext;
    private String currentFileExt;

    public ConvertProcess() {
    }

    public ConvertProcess(File file, FileExtension ext) {
        this.file = file;
        this.ext = ext;
    }

    public void execute() {
        ExtensionsUtil extUtil = new ExtensionsUtil();
        currentFileExt = FilenameUtils.getExtension(file.getAbsolutePath());
        FileExtension fileExtension = extUtil.getFileExtension(currentFileExt);
        FeatureSource fs = fileExtension.getFileProcess().readFile(file);

        String name = FilenameUtils.getBaseName(file.getAbsolutePath());
        File output = new File(file.getParent() + File.separator + name + "." + ext.getExtension());
        ext.getFileProcess().writeFile(fs, output);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public FileExtension getExt() {
        return ext;
    }

    public void setExt(FileExtension ext) {
        this.ext = ext;
    }

}
